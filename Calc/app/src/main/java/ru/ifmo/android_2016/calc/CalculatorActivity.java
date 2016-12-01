package ru.ifmo.android_2016.calc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.util.FastMath;

public class CalculatorActivity extends AppCompatActivity {

    float defaultTextSize;
    long currentNumber;
    long historyNumber;
    boolean clearFlag;
    String lastOp;

    protected void updateText(String newText, int field_id){
        TextView field = (TextView) findViewById(field_id);
        float expectedTextSize = findViewById(R.id.sizeExample).getWidth() * newText.length();
        float fontScale = 1.0f;
        while(expectedTextSize * fontScale > findViewById(R.id.format2).getWidth()){
            fontScale *= 0.9f;
        }
        field.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize * fontScale);
        field.setText(newText);
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("historyString", ((TextView) findViewById(R.id.history)).getText().toString());
        savedInstanceState.putLong("currentNumber", currentNumber);
        savedInstanceState.putLong("historyNumber", historyNumber);
        savedInstanceState.putString("lastOp", lastOp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String historyString;
        if(savedInstanceState != null){
            historyString = savedInstanceState.getString("historyString", "");
            currentNumber = savedInstanceState.getLong("currentNumber");
            historyNumber = savedInstanceState.getLong("historyNumber");
            lastOp = savedInstanceState.getString("lastOp", "+");
        } else {
            historyString = "";
            currentNumber = 0;
            historyNumber = 0;
            lastOp = "+";
        }
        setContentView(R.layout.activity_calculator);
        TextView tw = (TextView)findViewById(R.id.format2);
        defaultTextSize = (tw).getTextSize();
        updateText(Long.toString(currentNumber), R.id.result);
        updateText(historyString, R.id.history);
    }

    public void onDigitPress(View view){
        if(clearFlag){
            clearState(null);
        }
        long pressedDigit = Long.parseLong(((Button)view).getText().toString());
        long newNumber;
        try{
            newNumber = FastMath.multiplyExact(currentNumber, 10L);
            newNumber = FastMath.addExact(newNumber, pressedDigit);
            currentNumber = newNumber;
            updateText(((Long)currentNumber).toString(), R.id.result);
        } catch (MathArithmeticException e){
            Toast overflowToast = Toast.makeText(getBaseContext(), "Typed number is too big. Reset number to 0", Toast.LENGTH_SHORT);
            overflowToast.show();
            currentNumber = 0;
            updateText(((Long)currentNumber).toString(), R.id.result);
        }
    }

    public void clearState(View view){
        currentNumber = 0;
        historyNumber = 0;
        lastOp = "+";
        clearFlag = false;
        updateText("", R.id.history);
        updateText("0", R.id.result);
    }

    public void onActionPress(View view){
        long newNumber = 0;
        try{
            switch(lastOp){
                case "+":
                    newNumber = FastMath.addExact(historyNumber, currentNumber);
                    break;
                case "-":
                    newNumber = FastMath.subtractExact(historyNumber, currentNumber);
                    break;
                case "*":
                    newNumber = FastMath.multiplyExact(historyNumber, currentNumber);
                    break;
                case "/":
                    newNumber = FastMath.floorDiv(historyNumber, currentNumber);
                    break;
            }
            String op = ((Button)view).getText().toString();
            if(op.equals("=")){
                clearState(null);
                clearFlag = true;
                currentNumber = newNumber;
                updateText(Long.toString(currentNumber), R.id.result);
                updateText("", R.id.history);
            } else {
                clearFlag = false;
                historyNumber = newNumber;
                currentNumber = 0;
                lastOp = op;
                updateText("0", R.id.result);
                updateText(Long.toString(newNumber) + " " + op, R.id.history);
            }
        } catch (MathArithmeticException e){
            Toast overflowToast = Toast.makeText(getBaseContext(), "Result number is too big. Operation undone.", Toast.LENGTH_SHORT);
            overflowToast.show();
            currentNumber = 0;
            updateText(((Long)currentNumber).toString(), R.id.result);
        }
    }
}
