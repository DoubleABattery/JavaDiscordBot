import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CommandManager extends ListenerAdapter {




    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        User user = event.getUser();
        EmbedBuilder builder = new EmbedBuilder();


        switch (command) {
            case "hi" -> event.reply("Hi, **" + user.getName() + "**, how are you doing?").setEphemeral(true).queue();
            case "brainrot" -> {
                OptionMapping messageOption = event.getOption("user");
                if (messageOption != null){
                    User messageUser = messageOption.getAsUser();
                    int userCount = EventListener.userStores.get(messageUser.getId());
                    if (userCount == 1){
                        event.reply("**"+messageUser.getName()+"** has said a total of **" + userCount + "** brainrot word.").queue();
                    }
                    else{
                        event.reply("**"+messageUser.getName()+"** has said a total of **" + userCount + "** brainrot words.").queue();
                    }

                } else {
                    int userCount = EventListener.userStores.get(user.getId());
                    if (userCount == 1){
                        event.reply("You have said a total of **" + userCount + "** brainrot word.").queue();
                    } else {
                        event.reply("You have said a total of **" + userCount + "** brainrot words.").queue();
                    }
                }
            }

            case "brainrotleaderboard" -> {

                builder.setTitle("Brainrot Leaderboard")
                    .setDescription("Users with highest brainrot words said")
                    .setColor(Color.BLUE);


                HashMap<String, Integer> memberCounts = new HashMap<>();

                List<User> members = new ArrayList<>();
                Objects.requireNonNull(event.getGuild()).getMembers().forEach(member -> members.add(member.getUser()));

                for (Map.Entry<String, Integer> entry : EventListener.userStores.entrySet()){
                    if (members.contains(EventListener.aaBot.getShardManager().getUserById(entry.getKey()))) {
                        memberCounts.put(entry.getKey(), entry.getValue());
                    }
                }

                List<Integer> counts = new ArrayList<>();
                memberCounts.forEach((key, value) -> counts.add(value));

                counts.sort((x, y) -> { return -1 * x.compareTo(y); });

                LinkedHashMap<String, Integer> sortedMembers = new LinkedHashMap<>();
                for (int count : counts) {
                    for (Map.Entry<String, Integer> entry : memberCounts.entrySet()) {
                        if (entry.getValue().equals(count)) {
                            sortedMembers.put(entry.getKey(), count);
                        }
                    }
                }

                int i = 1;
                for (Map.Entry<String, Integer> entry : sortedMembers.entrySet()) {
                    if (entry.getValue()!=0)
                    {
                        if (entry.getValue() == 1) {
                            builder.addField(i+". "+Objects.requireNonNull(event.getGuild().getMemberById(entry.getKey())).getUser().getName(), entry.getValue()+" word", false);
                        } else {
                            builder.addField(i+". "+Objects.requireNonNull(event.getGuild().getMemberById(entry.getKey())).getUser().getName(), entry.getValue()+" words", false);
                        }

                        i++;
                        if (i>10) {
                            break;
                        }
                    }
                }

                event.replyEmbeds(builder.build()).queue();

            }
        }
    }
}