package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.TextFieldFormatter;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Client;
import model.exceptions.ValidationException;
import model.services.ClientService;

public class ClientFormController implements Initializable {

	private Client entity;

	private ClientService service;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtid;

	@FXML
	private TextField txtname;

	@FXML
	private TextField txttelephone;

	@FXML
	private DatePicker dpbirthdate;

	@FXML
	private TextField txtcpf;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorTelephone;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorCpf;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	// Dependências através do metodo set()
	public void setClient(Client entity) {
		this.entity = entity;
	}

	public void setServices(ClientService service) {
		this.service = service;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	// fim das dependências

	// Ações dos butões
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was Null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was Null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}

	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@FXML
	public void onTxtTelephoneAction() {
		TextFieldFormatter tff = new TextFieldFormatter();
		tff.setMask("(##)####-####");
		tff.setCaracteresValidos("0123456789");
		tff.setTf(txttelephone);
		tff.formatter();
	}
	
	@FXML
	public void onTxtCpfAction() {
		TextFieldFormatter tff = new TextFieldFormatter();
		tff.setMask("###.###.###-##");
		tff.setCaracteresValidos("0123456789");
		tff.setTf(txtcpf);
		tff.formatter();
	}

	// Funções auxiliares
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtid);
		Constraints.setTextFieldMaxLength(txtname, 70);
		Constraints.setTextFieldMaxLength(txttelephone, 60);
		Utils.formatDatePicker(dpbirthdate, "dd/MM/yyyy");
		Constraints.setTextFieldMaxLength(txtcpf, 15);

	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was Null");
		}
		txtid.setText(String.valueOf(entity.getId()));
		txtname.setText(entity.getName());
		txttelephone.setText(entity.getTelephone());
		if (entity.getBirthDate() != null) {
			dpbirthdate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		txtcpf.setText(entity.getCpf());
	}

	private Client getFormData() {
		Client obj = new Client();

		ValidationException exception = new ValidationException("Validation Error");

		obj.setId(Utils.tryParseToInt(txtid.getText()));

		if (txtname.getText() == null || txtname.getText().trim().equals("")) {
			exception.addError("name", "Favor Preencher! ");
		}
		obj.setName(txtname.getText());

		if (txttelephone.getText() == null || txttelephone.getText().trim().equals("")) {
			exception.addError("telephone", "Favor Preencher! ");
		}
		obj.setTelephone(txttelephone.getText());

		if (dpbirthdate.getValue() == null) {
			exception.addError("birthDate", "Favor Preencher! ");
		} else {
			Instant instant = Instant.from(dpbirthdate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}

		if (txtcpf.getText() == null || txtcpf.getText().trim().equals("")) {
			exception.addError("cpf", "Favor Preencher! ");
		}
		obj.setCpf(txttelephone.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		// Abaixo esta demonstrada outra forma de fazer um "if-else"
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));

		labelErrorTelephone.setText((fields.contains("telephone") ? errors.get("telephone") : ""));

		labelErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));

		labelErrorCpf.setText((fields.contains("cpf") ? errors.get("cpf") : ""));
	}

}
