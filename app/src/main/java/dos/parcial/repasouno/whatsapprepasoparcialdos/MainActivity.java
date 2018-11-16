package dos.parcial.repasouno.whatsapprepasoparcialdos;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText edt_nombre;
    //USUARIO ES EL EMAIL
    EditText edt_usuario;
    EditText edt_contrasena;
    Button btn_login;
    Button btn_registrar;

    //Autenticación
    FirebaseAuth auth;

    //Base de datos
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        edt_nombre= findViewById(R.id.edt_nombre);
        edt_usuario= findViewById(R.id.edt_usuario);
        edt_contrasena= findViewById(R.id.edt_contrasena);
        btn_login= findViewById(R.id.btn_login);
        btn_registrar= findViewById(R.id.btn_registrar);



        //Se inicializa la variable ella misma
        auth= FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();

        //Acceder a la base de datos
       final DatabaseReference reference= database.getReference();

        //Autenticarse o login
       // auth.createUserWithEmailAndPassword()


        //Asignarle el registrar a un botón
        btn_registrar.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {

                // Realizar las variables String
                final String nombre= edt_nombre.getText().toString();
               final String usuario= edt_usuario.getText().toString();
                final String contrasena= edt_contrasena.getText().toString();



                //autenticar
                //addOnCompleteListener me deja saber si fue bueno o no el registro

                auth.createUserWithEmailAndPassword(usuario,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {



                        if ((task.isSuccessful())){

                            String uid= auth.getCurrentUser().getUid();

                            //Crear un nuevo usuario para que se registre
                            Usuario user= new Usuario();

                            user.nombre= nombre;
                            user.usuario= usuario;
                            user.uid= uid;

                            //Reference es para entrar a la rama
                            //set value es para crearla

                            reference.child("Usuarios").child(uid).setValue(user);


                        }else{
                            Toast.makeText(MainActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();

                        }

                    }
                });



            }
        });

        //Asignarle el login a un botón
        btn_login.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {

                // Realizar las variables String --> SE COLOCA DONDE SE VAYA A HACER LA ACCION
                String nombre= edt_nombre.getText().toString();
                String usuario= edt_usuario.getText().toString();
                String contrasena= edt_contrasena.getText().toString();

                //autenticar CON SIGN
                //add on complete listener es para saber si quedó bueno el login o no
               auth.signInWithEmailAndPassword(usuario,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {

                       //Si no escribe el nombre bien entonces no se puede logear

                       //
                       if(task.isSuccessful()){

                           //Esto es para que vaya del login a la pantalla de chat

                           Intent irChat= new Intent(MainActivity.this,Chat.class);
                           startActivity(irChat);

                       }
                   }
               });
            }
        });

    }
}
