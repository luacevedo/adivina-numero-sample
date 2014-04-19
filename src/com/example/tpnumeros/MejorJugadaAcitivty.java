package com.example.tpnumeros;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class MejorJugadaAcitivty extends Activity {
	private static final String PUNTAJE = "puntaje";
	private static final String TITULO_PUNTAJE_ACTIVITY = "tituloPuntajeActivity";

	private TextView txtTitulo;
	private TextView txtPuntaje;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_mejor_puntaje);

		txtTitulo = (TextView) findViewById(R.id.mejorpuntaje_titulo);
		txtPuntaje = (TextView) findViewById(R.id.mejorpuntaje_puntaje);
		initialize();
	}

	private void initialize() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		txtPuntaje.setText(String.valueOf(preferences.getInt(PUNTAJE, 0)));

		String tituloRecibido = getIntent().getStringExtra(
				TITULO_PUNTAJE_ACTIVITY);
		txtTitulo.setText(tituloRecibido);
	}
}
