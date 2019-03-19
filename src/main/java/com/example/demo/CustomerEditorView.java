package com.example.demo;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@SpringComponent
@UIScope
public class CustomerEditorView extends VerticalLayout implements KeyNotifier {

    private final CustomerService customerService;

    private CustomerModel customerModel;

    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");

    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    HorizontalLayout actions = new HorizontalLayout(save, cancel);

    Binder<CustomerModel> binder = new Binder<>(CustomerModel.class);
    private ChangeHander changeHander;

    @Autowired
    public CustomerEditorView(CustomerService customerService){
        this.customerService = customerService;

        add(firstName, lastName, actions);

        binder.bindInstanceFields(this);

        setSpacing(true);

        save.getElement().getThemeList().add("primary");

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        cancel.addClickListener(e -> cancel());
        setVisible(false);
    }

    void delete(CustomerModel customerModel){
        customerService.deleteCustomer(customerModel.getPesel());
        changeHander.onChange();
    }

    void save(){
        customerService.addCustomer(customerModel);
        changeHander.onChange();
    }

    void cancel(){
        firstName.clear();
        lastName.clear();
        changeHander.onChange();
    }


    public interface ChangeHander{
        void onChange();
    }

    public final void editCustomer(CustomerModel c){
        if(c == null){
            setVisible(false);
            return;
        }
        final boolean persisted = c.getPesel() != null;
        if(persisted){
//            customerModel =  customerService.findCustomerByPesel(c.getPesel());
        }
        else{
            customerModel = c;
        }
        //was !persisted
        setVisible(true);

        binder.setBean(customerModel);

        firstName.focus();
    }

    public void setChangeHander(ChangeHander h){
        changeHander = h;
    }
}
