package fish.focus.uvms.activity.rest.message;

import fish.focus.uvms.commons.message.impl.JAXBUtils;
import fish.focus.wsdl.user.module.GetUserContextResponse;
import fish.focus.wsdl.user.types.*;
import fish.focus.uvms.activity.message.producer.ActivityResponseQueueProducerBean;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import static fish.focus.uvms.commons.message.api.MessageConstants.QUEUE_USM4UVMS;

@MessageDriven(mappedName = QUEUE_USM4UVMS, activationConfig = {
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = "javax.jms.MessageListener"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "UVMSUserEvent")})
public class UserServiceMock implements MessageListener {

    @Inject
    ActivityResponseQueueProducerBean messageProducer;

    @Override
    public void onMessage(Message message) {
        try {
            Role role = new Role();
            role.setRoleName("myRole");

            Scope scope = new Scope();

            Context context = new Context();
            context.setRole(role);
            context.setScope(scope);

            ContextSet contextSet = new ContextSet();
            contextSet.getContexts().add(context);

            UserContext userContext = new UserContext();
            userContext.setContextSet(contextSet);

            GetUserContextResponse response = new GetUserContextResponse();
            response.setContext(userContext);

            String stringResponse = JAXBUtils.marshallJaxBObjectToString(response);
            messageProducer.sendResponseMessageToSender((TextMessage) message, stringResponse);
        } catch (JMSException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
