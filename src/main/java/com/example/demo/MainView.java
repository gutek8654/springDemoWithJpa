package com.example.demo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

@Route
public class MainView extends VerticalLayout {

    private final CustomerService customerService;
    private final CustomerEditorView editor;
    final Grid<CustomerModel> grid;
    private final Button addNewBtn;
    private ListDataProvider<CustomerModel> dataProvider;
    private TextField firstNameFilterField;
    private TextField lastNameFilterField;

    public MainView(CustomerService customerService, CustomerEditorView editor){
        this.customerService = customerService;
        this.editor = editor;
        this.dataProvider = getData();
        this.grid = createGrid();

        this.addNewBtn = new Button("New customer", VaadinIcon.PLUS.create());
        addNewBtn.addClickListener(e -> editor.editCustomer(new CustomerModel()));

        editor.setChangeHander(() -> {
            editor.setVisible(false);
            refreshDataProvider();
        });

        HorizontalLayout actions = new HorizontalLayout(addNewBtn);

        add(actions, grid, editor);

        refreshDataProvider();
    }

    private Button deleteButton(CustomerModel customerModel){
        Button button = new Button(VaadinIcon.TRASH.create());
        button.addClickListener(e -> editor.delete(customerModel));
        button.getElement().getThemeList().add("error");
        return button;
    }

    private ListDataProvider<CustomerModel> getData(){

        return new ListDataProvider<>(customerService.getAllCustomers());
    }

    private Grid<CustomerModel> createGrid(){
        Grid<CustomerModel> grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        Grid.Column<CustomerModel> firstNameColumn = grid.addColumn(CustomerModel::getFirstName).setHeader("First Name");
        Grid.Column<CustomerModel> lastNameColumn = grid.addColumn(CustomerModel::getLastName).setHeader("Last Name");
        grid.addComponentColumn(this::deleteButton);
        grid.addItemDoubleClickListener(listener -> {
            editor.editCustomer(listener.getItem());
        });

        HeaderRow filterRow = grid.appendHeaderRow();

        firstNameFilterField = new TextField();
        firstNameFilterField.addValueChangeListener(e -> dataProvider.addFilter(
                customer -> StringUtils.containsIgnoreCase(customer.getFirstName(), firstNameFilterField.getValue())
        ));
        firstNameFilterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(firstNameColumn).setComponent(firstNameFilterField);
        firstNameFilterField.setSizeFull();
        firstNameFilterField.setPlaceholder("Filter");

        lastNameFilterField = new TextField();
        lastNameFilterField.addValueChangeListener(e -> dataProvider.addFilter(
                customer -> StringUtils.containsIgnoreCase(customer.getLastName(), lastNameFilterField.getValue())
        ));
        lastNameFilterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterRow.getCell(lastNameColumn).setComponent(lastNameFilterField);
        lastNameFilterField.setSizeFull();
        lastNameFilterField.setPlaceholder("Filter");

        return grid;
    }

    private void refreshDataProvider(){
        firstNameFilterField.clear();
        lastNameFilterField.clear();
        dataProvider = getData();
        grid.setDataProvider(dataProvider);
    }
}
