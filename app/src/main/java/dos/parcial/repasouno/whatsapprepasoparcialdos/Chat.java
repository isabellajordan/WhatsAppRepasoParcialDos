package dos.parcial.repasouno.whatsapprepasoparcialdos;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Chat extends AppCompatActivity {

    EditText chatear;
    Button enviar;
    ListView lista;

    FirebaseDatabase database;

    //Para leer al usuario
    String nameUser;

    //Crear la clase para la lista de Firebase

    FirebaseListAdapter listAdapter;

    //Autenticarse

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        //Iniciar el auth
        auth.getInstance();

        chatear= findViewById(R.id.edt_enviarmensaje);
        enviar= findViewById(R.id.btn_enviar);
        lista= findViewById(R.id.lv_contactos);

        //Colocar la base de datos y entrar en ella
database.getInstance();

DatabaseReference reference= database.getReference();

//Entrar a las ramas para leer lo que hay dentro

        //AQUÍ SE CREA LA CARPETA "CHATS"
       Query chat= reference.child("Chats");



        //Se tiene que crear primero el FirebaseListOptions

        FirebaseListOptions options= new FirebaseListOptions.Builder<Mensaje>().setLayout(R.layout.renglon).setQuery(chat, Mensaje.class).build();

        listAdapter= new FirebaseListAdapter<Mensaje>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Mensaje model, final int position) {

                //En el view se pone la actualización del mensaje

                TextView nombre= v.findViewById(R.id.tv_usuario);
                TextView mensaje= v.findViewById(R.id.tv_mensaje);

                //AQUI SE COLOCA LO QUE ESTÁ EN EL MODELO O SEA MENSAJE
                nombre.setText(model.name);
                mensaje.setText(model.mensaje);


                //


                //Si le doy click al view
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        //PARA ELIMINAR LOS MENSAJES
                        listAdapter.getRef(position).removeValue();

                    }
                });


            }
        };

        //AQUI SE PUBLICA LA LISTA
lista.setAdapter(listAdapter);

//ESTO ERA LO QUE HABÍA EN EL ONCLICK PERO SE PEGÓ FUERA PORQUE SÓLO SE VA A UTILIZAR UNA VEZ

        //Esto obtiene la sesión para obtener los datos de los usuarios
        //Devuelve la sesión pero no la ubicación HAY QUE HACER LA ESTRATEGIA DE IR A BUSCAR EN LA BASE DE DATOS
        FirebaseUser user= auth.getCurrentUser();

        //aquí obtenemos la ubicación de los usuarios
        DatabaseReference ref= database.getReference().child("Usuarios").child(user.getUid());

        //Para obtener usuario

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot dato: dataSnapshot.getChildren()){
                    Usuario user= dato.getValue(Usuario.class);
                    nameUser= user.nombre;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






//Enviar el mensaje a través del botón
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Coger el mensaje y darle enviar

                String mensaje= chatear.getText().toString();
                Mensaje m = new Mensaje();

                m.name= nameUser;

                m.mensaje= mensaje;


                //Se debe navegar por la base de datos

                DatabaseReference publicar= database.getReference();
                publicar.child("Chat").push().setValue(m);

            }


        });


    }

    @Override
    protected void onStart() {
        listAdapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        listAdapter.stopListening();
        super.onStop();
    }
}
