package com.example.tpnumeros;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	private static final int INTENTOS_DIFICULTAD_DIFICIL = 5;
	private static final int INTENTOS_DIFICULTAD_MEDIA = 10;
	private static final int INTENTOS_DIFICULTAD_FACIL = 20;

	private int dificultad;
	private EditText edtNumero;
	private int numeroGenerado[];
	private int numeroIngresado[];
	private int numerosBien;
	private int numerosRegular;
	private int intentos;
	private LinearLayout layoutMensajes;
	private ArrayList<TextView> txtIntentos;

	private TextView txtResultado;
	private Button btnOK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Este método se ejecuta siempre que una Acitivty se inicia. Acá se
		// debería inicializar casi todo

		super.onCreate(savedInstanceState);

		// De esta forma le asigno a la activity un layout definido por XML
		setContentView(R.layout.activity_main);

		// Así se referencia a un componente especificado en el XML para luego
		// darle lógica
		edtNumero = (EditText) findViewById(R.id.activity_main_edt_number);
		layoutMensajes = (LinearLayout) findViewById(R.id.activity_main_layout_contenedor);
		txtIntentos = new ArrayList<TextView>();
		btnOK = (Button) findViewById(R.id.activity_main_btn_listo);

		// Se asigna un ClickListener al botón para agregar funcionalidad. Se
		// puede crear una clase que implemente ClickListener o incluse hacer
		// que esta misma activity lo implemente
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				validar();
			}
		});

		this.intentos = 0;
		generarNumeroParaAdivinar();

		txtResultado = (TextView) findViewById(R.id.activity_main_tv_titulo);
		int numeroParaAdivinarFormateado = (this.numeroGenerado[0] * 1000)
				+ (this.numeroGenerado[1] * 100) + (this.numeroGenerado[2] * 10)
				+ this.numeroGenerado[3];
		txtResultado.setText(String.format(getResources().getString(R.string.respuesta),
				numeroParaAdivinarFormateado));
	}

	public void iniciarVector(int[] vector) {
		int i;
		for (i = 0; i < 4; i++) {
			vector[i] = -1;
		}

	}

	public void generarNumeroParaAdivinar() {
		this.numeroGenerado = new int[4];
		Random r = new Random();

		int primerNumero;
		primerNumero = primerNumeroDistintoDeCero(r);

		this.numeroGenerado[0] = primerNumero;

		completarTodosLosDigitos(r);
	}

	private void completarTodosLosDigitos(Random r) {
		int i;
		int numero;
		for (i = 1; i < 4; i++) {
			do {
				numero = r.nextInt(9);
			} while (fueUtilizado(this.numeroGenerado, numero));

			this.numeroGenerado[i] = numero;
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
			Toast.makeText(this, R.string.no_ingreso_ningun_numero, Toast.LENGTH_SHORT).show();
		} else {
			int num = Integer.parseInt(edtNumero.getText().toString());
			this.numeroIngresado = this.validarNum(num);

			if (this.numeroIngresado != null) {
				if (esGanador()) {
					habilitarUI(false);
					mostrarDialogGanador();
				} else {
					if (terminoJuego()) {
						habilitarUI(false);
						mostrarDialogFinDeJuego();
					} else {
						mostrarResultado();
					}
				}

			}
		}

	}

	private void mostrarDialogGanador() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(String.format(getResources().getString(R.string.ganaste_en),
				this.intentos, this.edtNumero.getText().toString()));
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				juegoNuevo();
			}
		});
		builder.setNegativeButton(R.string.no, null);
		builder.show();
	}

	private void mostrarResultado() {
		TextView txtEstadoDeJugada = new TextView(this);

		int numero = (this.numeroIngresado[0] * 1000) + (this.numeroIngresado[1] * 100)
				+ (this.numeroIngresado[2] * 10) + this.numeroIngresado[3];

		txtEstadoDeJugada.setText(String.format(getResources().getString(R.string.estado_jugada),
				numero, this.numerosBien, this.numerosRegular));

		// Especifica el tamaño del texto
		txtEstadoDeJugada.setTextSize(15);

		this.txtIntentos.add(txtEstadoDeJugada);
		this.layoutMensajes.addView(txtEstadoDeJugada);
		this.edtNumero.setText("");
	}

	private void mostrarDialogFinDeJuego() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.fin_del_juego_iniciar_juego_nuevo);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				juegoNuevo();
			}
		});
		builder.setNegativeButton(R.string.no, null);
		builder.show();
	}

	private void mostrarDialogReset() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.reiniciar_partida);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				juegoNuevo();
			}
		});
		builder.setNegativeButton(R.string.no, null);
		builder.show();
	}

	private boolean terminoJuego() {
		return this.intentos == dificultad;
	}

	public int[] validarNum(int num) {
		try {
			int vector[] = new int[4];
			iniciarVector(vector);

			if (num < PRIMER_NUMERO_VALIDO) {
				// No hace falta poner todos los strings adentro del archivo
				// strings.xml aunque eso hará que este string no se pueda
				// localizar
				Toast.makeText(this, "El número no es valido", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(this, R.string.el_numero_no_puede_tener_digitos_repetidos,
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
		this.numerosBien = 0;
		this.numerosRegular = 0;
		for (i = 0; i < 4; i++) {
			if (fueUtilizado(this.numeroGenerado, this.numeroIngresado[i])) {
				pos = this.darPosicion(numeroGenerado, numeroIngresado[i]);
				if (pos == i)
					this.numerosBien++;
				else
					this.numerosRegular++;
			}
		}
		this.intentos++;
		return (numerosBien == 4);
	}

	public int darPosicion(int vec[], int num) {
		int i = 0;
		while (i < vec.length && vec[i] != num)
			i++;

		return i;

	}

	public void juegoNuevo() {
		habilitarUI(true);

		this.generarNumeroParaAdivinar();
		int i;
		for (i = 0; i < this.txtIntentos.size(); i++) {
			this.txtIntentos.get(i).setText("");
		}
		this.intentos = 0;
		this.txtIntentos.clear();
		this.edtNumero.setText("");
		int n = (this.numeroGenerado[0] * 1000) + (this.numeroGenerado[1] * 100)
				+ (this.numeroGenerado[2] * 10) + this.numeroGenerado[3];
		this.txtResultado.setText(R.string.respuesta + n);

	}

	private void habilitarUI(boolean habilitar) {
		edtNumero.setEnabled(habilitar);
		btnOK.setEnabled(habilitar);

	}

	public void setDificultad(int dificultad) {
		this.dificultad = dificultad;
		juegoNuevo();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Agrega items a la action bar si es que está presente
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemNuevo:
			mostrarDialogReset();
			return true;
		case R.id.itemFacil:
			setDificultad(INTENTOS_DIFICULTAD_FACIL);
			return true;
		case R.id.itemMedio:
			setDificultad(INTENTOS_DIFICULTAD_MEDIA);
			return true;
		case R.id.itemDificil:
			setDificultad(INTENTOS_DIFICULTAD_DIFICIL);
			return true;
		default:
			return false;
		}

	}
}
