package ma.ensa.www.assistdoc.patient;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectSpinner extends androidx.appcompat.widget.AppCompatSpinner {

    private List<String> items;
    private boolean[] selected;
    private String defaultText = "Select Symptoms";
    private ArrayAdapter<String> adapter;

    public MultiSelectSpinner(Context context) {
        super(context);
        initializeAdapter(context);
    }

    public MultiSelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeAdapter(context);
    }

    private void initializeAdapter(Context context) {
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        adapter.add(defaultText);
        setAdapter(adapter);
    }

    public void setItems(List<String> items) {
        this.items = new ArrayList<>(items); // Ensure the list is modifiable
        this.selected = new boolean[items.size()];
        for (int i = 0; i < items.size(); i++) {
            selected[i] = false; // Initialize all as unselected
        }
        adapter.clear();
        adapter.add(defaultText); // Reset the spinner text
    }

    public List<Integer> getSelectedItems() {
        List<Integer> selectedItems = new ArrayList<>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                selectedItems.add(i);
            }
        }
        return selectedItems;
    }

    @Override
    public boolean performClick() {
        showMultiSelectDialog();
        return true;
    }

    private void showMultiSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Symptoms");

        builder.setMultiChoiceItems(items.toArray(new CharSequence[0]), selected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index, boolean isChecked) {
                selected[index] = isChecked;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateSpinnerText();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    public void updateSpinnerText() {
        List<String> displayItems = new ArrayList<>(); // Use a mutable list for selected items
        for (int i = 0; i < items.size(); i++) {
            if (selected[i]) {
                displayItems.add(items.get(i));
            }
        }

        adapter.clear();

        if (displayItems.isEmpty()) {
            adapter.add(defaultText); // No item selected, use default text
        } else {
            adapter.add(String.join(", ", displayItems)); // Display selected items
        }
    }
}
