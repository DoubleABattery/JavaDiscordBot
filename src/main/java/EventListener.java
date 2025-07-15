import java.io.*;
import java.util.*;
import com.google.gson.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;


public class EventListener extends ListenerAdapter {
    private static String FILE_NAME;
    public static Bot aaBot;
    public static HashMap<String, Integer> userStores = new HashMap<>();
    public static String[] brainrot = {"rizz", "skibidi", "gyatt", "sigma", "fanum", "tuah", "goon", "ohio", "low taper fade", "maxing", "balkan", "winter arc"};

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        try {
            File file = new File("src/main/counts.json");
            file.createNewFile();
            FILE_NAME = file.getAbsolutePath();
        } catch (IOException e){
            System.err.println("An error occurred while creating the file: "+ e.getMessage());
        }
        try (Reader reader = new FileReader(FILE_NAME)) {
            Map data = new Gson().fromJson(reader, Map.class);
            if (data != null) {
                data.forEach((key, value) -> {
                    userStores.put((String) key, ((Double) value).intValue());
                });
            } else {
                for (Guild guild : EventListener.aaBot.getShardManager().getGuilds()){
                    for (Member member : guild.getMembers()){
                        if (!member.getUser().isBot()){
                            userStores.putIfAbsent(member.getUser().getId(), 0);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Data file not found, starting fresh.");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("hi", "Greet the bot."));
        commandData.add(Commands.slash("brainrot", "Get the total number of brainrot words you've said.").addOption(OptionType.USER, "user", "Which user do you want to check?", false));
        commandData.add(Commands.slash("brainrotleaderboard", "Get the list of the people in the server who have used brainrot words"));
        event.getJDA().updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onSessionDisconnect(@NotNull SessionDisconnectEvent event) {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            new Gson().toJson(userStores, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        User user = event.getAuthor();
        String message = event.getMessage().getContentRaw();

        message = message.toLowerCase();
        for (String word : brainrot){
            if (message.contains(word)){

                if (!userStores.containsKey(user.getId())){
                    userStores.put(user.getId(), 0);
                }
                userStores.replace(user.getId(), userStores.get(user.getId())+1);

                try (Writer writer = new FileWriter(FILE_NAME)) {
                    new Gson().toJson(userStores, writer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                event.getChannel().sendMessage(user.getName()+" has added to their brainrot by saying "+word+"! Total brainrot words said: "+userStores.get(user.getId())).queue();
            }
        }
    }
}
