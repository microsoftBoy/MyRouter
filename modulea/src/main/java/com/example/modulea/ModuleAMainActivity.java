package com.example.modulea;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.shuaige.myrouter.facade.annotation.Route;
import com.example.shuaige.myrouter.launcher.MyRouter;

@Route(path = "/modulea/moduleamain")
public class ModuleAMainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_amain);

        button = (Button) findViewById(R.id.button111);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button111) {
            MyRouter.getInstance().build("/moduleb/modulebmain").navigation();

        }
    }
}
