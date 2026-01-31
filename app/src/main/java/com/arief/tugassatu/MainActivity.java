package com.arief.tugassatu;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etA, etB;
    private Spinner spOp;
    private TextView tvResult, tvHistoryHint;
    private MaterialButton btnHitung, btnClear, btnClearHistory;
    private com.google.android.material.card.MaterialCardView cardCalc;
    private android.widget.ScrollView svMain;


    private final DecimalFormat df = new DecimalFormat("#.##########");

    private final ArrayList<HistoryItem> history = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardCalc = findViewById(R.id.cardCalc);
        svMain = findViewById(R.id.svMain);
        svMain.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int y = svMain.getScrollY();
            float lift = Math.min(18f, y / 12f);
            cardCalc.setCardElevation(6f + lift);
            cardCalc.setTranslationY(-Math.min(10f, y / 25f));
        });
        etA = findViewById(R.id.etA);
        etB = findViewById(R.id.etB);
        spOp = findViewById(R.id.spOp);
        tvResult = findViewById(R.id.tvResult);

        btnHitung = findViewById(R.id.btnHitung);
        btnClear = findViewById(R.id.btnClear);

        tvHistoryHint = findViewById(R.id.tvHistoryHint);
        btnClearHistory = findViewById(R.id.btnClearHistory);

        RecyclerView rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(history);
        rvHistory.setAdapter(adapter);

        btnHitung.setOnClickListener(v -> hitungDanSimpan());
        btnClear.setOnClickListener(v -> clearInput());
        btnClearHistory.setOnClickListener(v -> clearHistory());

        refreshHistoryHint();
    }

    private void hitungDanSimpan() {
        String aStr = safeText(etA);
        String bStr = safeText(etB);

        if (aStr.isEmpty() || bStr.isEmpty()) {
            toast("Input tidak boleh kosong.");
            return;
        }

        Double a = parse(aStr);
        Double b = parse(bStr);

        if (a == null || b == null) {
            toast("Input harus angka yang valid.");
            return;
        }

        int opIndex = spOp.getSelectedItemPosition();
        String symbol;
        Double result;

        switch (opIndex) {
            case 0: // +
                symbol = "+";
                result = a + b;
                break;
            case 1: // -
                symbol = "−";
                result = a - b;
                break;
            case 2: // ×
                symbol = "×";
                result = a * b;
                break;
            case 3: // ÷
                symbol = "÷";
                if (b == 0.0) {
                    tvResult.setText(formatSentence(a, symbol, b, "Error"));
                    toast("Tidak bisa membagi dengan 0.");
                    addHistory(formatSentence(a, symbol, b, "Error"));
                    return;
                }
                result = a / b;
                break;
            default:
                toast("Operasi tidak dikenali.");
                return;
        }

        String sentence = formatSentence(a, symbol, b, df.format(result));
        tvResult.setText(sentence);
        addHistory(sentence);
    }

    private String formatSentence(double a, String symbol, double b, String resultText) {
        return df.format(a) + " " + symbol + " " + df.format(b) + " = " + resultText;
    }

    private void addHistory(String line) {
        // biar history terbaru di atas
        history.add(0, new HistoryItem(line));
        adapter.notifyItemInserted(0);
        refreshHistoryHint();
    }

    private void clearInput() {
        etA.setText("");
        etB.setText("");
        tvResult.setText("—");
    }

    private void clearHistory() {
        history.clear();
        adapter.notifyDataSetChanged();
        refreshHistoryHint();
    }

    private void refreshHistoryHint() {
        tvHistoryHint.setText(history.isEmpty() ? "Belum ada perhitungan." : "Terbaru di atas.");
    }

    private String safeText(TextInputEditText et) {
        if (et.getText() == null) return "";
        return et.getText().toString().trim().replace(",", ".");
    }

    private Double parse(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}