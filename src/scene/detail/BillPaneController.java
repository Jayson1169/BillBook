package scene.detail;

import domain.Bill;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import util.Util;

import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class BillPaneController implements Initializable {
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField amountInput;
    @FXML
    private TextField remarkInput;
    @FXML
    private ComboBox<String> categoryCombobox;
    @FXML
    private Button editButton;
    @FXML
    private HBox editPane;
    @FXML
    private Label dateLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label remarkLabel;

    private static int selectedIndex = -1;
    private int index;
    private Bill bill;

    private ArrayList<BillPaneController> controllers;

    private ObservableList<HBox> billPaneList;

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setControllers(ArrayList<BillPaneController> controllers) {
        this.controllers = controllers;
    }

    public void setBillPaneList(ObservableList<HBox> billPaneList) {
        this.billPaneList = billPaneList;
    }


    public void backView() {
        editPane.setVisible(false);
        amountInput.setVisible(false);
        categoryCombobox.setVisible(false);
        datePicker.setVisible(false);
        remarkInput.setVisible(false);
        editButton.setVisible(true);
    }

    @FXML
    public void onEdit() {
        editButton.setVisible(false);
        editPane.setVisible(true);
        amountInput.setVisible(true);
        remarkInput.setVisible(true);
        categoryCombobox.setVisible(true);
        datePicker.setVisible(true);

        amountInput.setText(Util.getDecimalFormat().format(Math.abs(bill.getAmount())));
        remarkInput.setText(bill.getRemark());
        categoryCombobox.setValue(bill.getCategory());
        datePicker.setValue(LocalDate.parse(Util.getDateFormat().format(bill.getDate())));
        if(selectedIndex != -1 && selectedIndex != index) {
            controllers.get(selectedIndex).backView();
        }
        selectedIndex = index;
    }

    @FXML
    public void onDelete() {
        billPaneList.remove(index);
        controllers.remove(index);
        for(int i = index; i < controllers.size(); i ++) {
            controllers.get(i).setIndex(controllers.get(i).getIndex() - 1);
        }
        Util.getBillDAO().deleteBill(bill);
    }

    @FXML
    public void onSave() {
        String amountString = amountInput.getText();
        if (!Util.isAmountLegal(amountString)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setTitle("提示");
            alert.setContentText("金额输入有误，请重试！");
            alert.showAndWait();
            return;
        }

        double amount = Double.parseDouble(amountString);
        bill.setAmount(bill.getAmount() > 0 ? amount : -amount);
        try {
            bill.setDate(Util.getDateFormat().parse(datePicker.getValue().toString()));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        bill.setRemark(remarkInput.getText());
        bill.setCategory(categoryCombobox.getValue());

        Util.getBillDAO().updateBill(bill);
        dateLabel.setText(Util.getDateFormat().format(bill.getDate()));
        amountLabel.setText(amountInput.getText());
        categoryLabel.setText(categoryCombobox.getValue());
        remarkLabel.setText(remarkInput.getText());

        backView();
    }

    @FXML
    public void onCancel() {
        backView();
    }

    public void setRemark(String text) {
        remarkLabel.setText(text);
    }

    public void setDate(Date date) {
        LocalDate localDate = LocalDate.parse(Util.getDateFormat().format(date));
        LocalDate today = LocalDate.now();
        if (today.isEqual(localDate))
            dateLabel.setText("今天");
        else if (today.minusDays(1).isEqual(localDate))
            dateLabel.setText("昨天");
        else if (today.minusDays(2).isEqual(localDate))
            dateLabel.setText("前天");
        else
            dateLabel.setText(localDate.toString());
    }

    public void setAmount(double amount) {
        if(amount > 0) {
            categoryCombobox.getItems().addAll(Bill.INCOME_CATEGORIES);
            amountLabel.setText("+" + Util.getDecimalFormat().format(amount));
            amountLabel.setTextFill(Color.RED);
        }
        else {
            categoryCombobox.getItems().addAll(Bill.EXPENDITURE_CATEGORIES);
            amountLabel.setText(Util.getDecimalFormat().format(amount));
            amountLabel.setTextFill(Color.GRAY);
        }
    }

    public void setCategory(String text) {
        categoryLabel.setText(text);
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            LocalDate localDate = LocalDate.now();
            if (datePicker.getValue().isAfter(localDate))
                datePicker.valueProperty().setValue(localDate);
        });
    }
}
