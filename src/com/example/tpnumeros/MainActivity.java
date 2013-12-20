package com.example.tpnumeros;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int PRIMER_NUMERO_VALIDO = 1023;
	private static final int LONGITUD_DEL_NUMERO = 4;
	private static final int DIFICULTAD_DIFICIL = 5;
	private static final int DIFICULTAD_MEDIA = 10;
	private static final int DIFICULTAD_FACIL = 20;

	private int dificultad;
	private EditText edtNumero;
	private int numeroCreado[];
	private int numIngresado[];
	private int bien, regular, intentos;
	private LinearLayout layoutMensajes;
	private ArrayList<TextView> tvIntentos;

	private TextView tvResultado;
	private Button btnOK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		edtNumero = (EditText) findViewById(R.id.activity_main_edt_number);
		layoutMensajes = (LinearLayout) findViewById(R.id.activity_main_layout_contenedor);
		tvIntentos = new ArrayList<TextView>();
		btnOK = (Button) findViewById(R.id.activity_main_btn_listo);

		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				validar();
			}
		});

		generarNumero();
		tvResultado = (TextView) findViewById(R.id.activity_main_tv_titulo);

		int n = (this.numeroCreado[0] * 1000) + (this.numeroCreado[1] * 100)
				+ (this.numeroCreado[2] * 10) + this.numeroCreado[3];

		tvResultado.setText("Respuesta: " + n);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void iniciarVec(int[] vec) {
		int i;
		for (i = 0; i < 4; i++)
			vec[i] = -1;

	}

	public void generarNumero() {
		// Generar el Numero
		this.intentos = 0;
		this.numeroCreado = new int[4];
		Random r = new Random();

		int primerNumero;
		primerNumero = primerNumeroDistintoDeCero(r);

		this.numeroCreado[0] = primerNumero;

		completarTodosLosDigitos(r);
	}

	private void completarTodosLosDigitos(Random r) {
		int i;
		int numero;
		for (i = 1; i < 4; i++) {
			do
				numero = r.nextInt(9);
			while (fueUtilizado(this.numeroCreado, numero));
			this.numeroCreado[i] = numero;
		}
	}

	private int primerNumeroDistintoDeCero(Random r) {
		int num;
		do {
			num = r.nextInt(9);
		} while (num < 1);

		return num;
	}

	public boolean fueUtilizado(int vec[], int num) {
		boolean encontrado = false;
		int i;
		for (i = 0; i < LONGITUD_DEL_NUMERO; i++) {
			if (vec[i] == num)
				encontrado = true;
		}
		return encontrado;
	}

	public void validar() {
		if (this.edtNumero.getText().toString().trim().equals("")) {
			Toast.makeText(this, "No se ingreso ningun numero",
					Toast.LENGTH_SHORT).show();
		} else {
			int num = Integer.parseInt(edtNumero.getText().toString());
			this.numIngresado = this.validarNum(num);

			if (this.numIngresado != null) {
				if (esGanador()) {
					habilitarUI(false);
					mostrarDialogGanador();
				} else {
					if (terminoJuego()) {
						habilitarUI(false);
						inicializarDialogFinDeJuego();
					} else {
						mostrarResultado();
					}
				}

			}
		}

	}

	private void mostrarDialogGanador() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Ganaste en " + this.intentos
				+ " intentos! el numero era: "
				+ this.edtNumero.getText().toString()
				+ ". Iniciar juego nuevo?");
		builder.setCancelable(false);
		builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				juegoNuevo();
			}
		});
		builder.setNegativeButton("No", null);
		builder.show();
	}

	private void mostrarResultado() {
		TextView mensaje = new TextView(this);
		int numero = (this.numIngresado[0] * 1000)
				+ (this.numIngresado[1] * 100) + (this.numIngresado[2] * 10)
				+ this.numIngresado[3];
		mensaje.setText(numero + ", tiene " + this.bien + " bien y "
				+ this.regular + " regular");
		mensaje.setTextSize(15);
		this.tvIntentos.add(mensaje);
		this.layoutMensajes.addView(mensaje);
		this.edtNumero.setText("");
	}

	private void inicializarDialogFinDeJuego() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Fin del juego. Iniciar juego nuevo?");
		builder.setCancelable(false);
		builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				juegoNuevo();
			}
		});
		builder.setNegativeButton("No", null);
		builder.show();
	}

	private boolean terminoJuego() {
		return this.intentos == dificultad;
	}

	public int[] validarNum(int num) {
		try {
			int vector[] = new int[4];
			iniciarVec(vector);

			if (num < PRIMER_NUMERO_VALIDO) {
				Toast.makeText(this, "El número no es valido",
						Toast.LENGTH_SHORT).show();
				return null;
			} else {

				return validarNumeroRepetido(num, vector);
			}
		} catch (Exception e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
	}

	private int[] validarNumeroRepetido(int num, int[] vector) {
		int i;
		for (i = 3; i >= 0; i--) {
			int digito = num % 10;
			if (fueUtilizado(vector, digito)) {
				Toast.makeText(this,
						"El número no puede tener digitos repetidos",
						Toast.LENGTH_SHORT).show();
				return null;
			} else {
				vector[i] = digito;
				num = num / 10;
			}
		}
		return vector;
	}

	public boolean esGanador() {
		int i, pos;
		this.bien = 0;
		this.regular = 0;
		for (i = 0; i < 4; i++) {
			if (fueUtilizado(this.numeroCreado, this.numIngresado[i])) {
				pos = this.darPosicion(numeroCreado, numIngresado[i]);
				if (pos == i)
					this.bien++;
				else
					this.regular++;
			}
		}
		this.intentos++;
		return (bien == 4);
	}

	public int darPosicion(int vec[], int num) {
		int i = 0;
		while (i < vec.length && vec[i] != num)
			i++;

		return i;

	}

	public void juegoNuevo() {
		habilitarUI(true);

		this.generarNumero();
		int i;
		for (i = 0; i < this.tvIntentos.size(); i++) {
			this.tvIntentos.get(i).setText("");
		}
		this.intentos = 0;
		this.tvIntentos.clear();
		this.edtNumero.setText("");
		int n = (this.numeroCreado[0] * 1000) + (this.numeroCreado[1] * 100)
				+ (this.numeroCreado[2] * 10) + this.numeroCreado[3];
		this.tvResultado.setText("Respuesta: " + n);

	}

	private void habilitarUI(boolean habilitar) {
		edtNumero.setEnabled(habilitar);
		btnOK.setEnabled(habilitar);

	}

	public void dificultad(int dificultad) {
		// modifica el numero de intentos
		this.dificultad = dificultad;
		juegoNuevo();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemNuevo:
			juegoNuevo();
			return true;
		case R.id.itemFacil:
			dificultad(DIFICULTAD_FACIL);
			return true;
		case R.id.itemMedio:
			dificultad(DIFICULTAD_MEDIA);
			return true;
		case R.id.itemDificil:
			dificultad(DIFICULTAD_DIFICIL);
			return true;
		default:
			return false;
		}

	}

}
