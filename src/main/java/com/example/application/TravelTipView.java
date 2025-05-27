package com.example.application;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.ai.chat.client.ChatClient;

@Route("")
public class TravelTipView extends VerticalLayout {

    private static final String PROMPT_TEMPLATE = """
        You are an expert traveler with near infinite knowledge of the world.
        You love to give travel tips to others, and you are always happy to help.
        
        I am traveling to {destination} and I am interested in {interests}.
        Currently, I am feeling {mood}.
        
        What tips do you have for me?
        """;

    public TravelTipView(ChatClient.Builder builder) {
        var chatClient = builder
            .build();

        var ui = UI.getCurrent();
        var title = new H1("AI Travel Tips");
        var form = new HorizontalLayout() {{
            setWidthFull();
            setAlignItems(Alignment.BASELINE);
        }};
        var destination = new TextField("Destination");
        var interests = new TextField("Interests");
        var mood = new Select<String>() {{
            setLabel("Mood");
            setItems("Excited", "Curious", "Relaxed", "Adventurous", "Nervous");
        }};
        var submitButton = new Button("Get Travel Tips");
        var response = new Markdown("");

        form.add(destination, interests, mood, submitButton);

        submitButton.addClickListener(event -> {
            response.setContent("");

            chatClient
                .prompt()
                .user(u -> {
                    u.text(PROMPT_TEMPLATE);
                    u.param("destination", destination.getValue());
                    u.param("interests", interests.getValue());
                    u.param("mood", mood.getValue());
                })
                .stream()
                .content()
                .subscribe(token -> ui.access(() ->
                    response.appendContent(token)
                ));
        });
        add(title, form, response);
    }
}
