package ntnu.idatt2105.project.backend.service;

import lombok.RequiredArgsConstructor;
import ntnu.idatt2105.project.backend.model.Chat;
import ntnu.idatt2105.project.backend.model.Message;
import ntnu.idatt2105.project.backend.model.User;
import ntnu.idatt2105.project.backend.model.dto.MessageDTO;
import ntnu.idatt2105.project.backend.repository.ChatRepository;
import ntnu.idatt2105.project.backend.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    public List<MessageDTO> getMessages(Long chatId, User user){
        List<Message> messages = messageRepository.findByChatid(chatId.toString());
        List<MessageDTO> messageDTOs = messages.stream().map(message -> new MessageDTO(message, user)).collect(Collectors.toList());
        return messageDTOs;
    }

    public MessageDTO sendMessage(Long chatId, User user, String message){
        User recepient;
        Chat c = chatRepository.getReferenceById(chatId);
        if(c.getUserOne() == user){
            recepient = c.getUserTwo();
        }
        else{
            recepient = user;
        }
        Message messageObject = new Message();
        messageObject.setMessage(message);
        messageObject.setChat(c);
        messageObject.setSender(user);
        messageObject.setReceiver(recepient);

        messageRepository.save(messageObject);
        return new MessageDTO(messageObject, user);
    }
}
