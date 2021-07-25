package pl.cekcsecurity.server.gate.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import pl.cekcsecurity.server.gate.*;
import pl.cekcsecurity.server.gate.provider.GatekeeperResourceProvider;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BotGatekeeper extends ListenerAdapter implements LoaderGatekeeper {
    private static final String GATEKEEPER_CHANNEL_ID = "868523700545290281";
    private static final String GATEKEEPER_MESSAGE = "@everyon IJO IJO KTOS SIE CHCE POLACZYC!!!!!!!!\nREQUESTED PRODUKT: %s\nIP: %s";
    private static final String GATEKEEPER_TOKEN = "ODE3MDkzODA5MzAwODMyMjc3.YEEf6Q.nzIHKBQa1qwfD6CI_9B74W1Cikw";
    private static final String GATEKEEPER_MESSAGE_SUCCESS = "Request ukonczony!";

    private final JDA jda;

    private final Map<Long, CompletableFuture<LoaderResponseEnum>> longCompletableFutureMap = new HashMap<>();
    private final GatekeeperResourceProvider resourceProvider;

    public BotGatekeeper(GatekeeperResourceProvider resourceProvider) throws LoginException {
        this.resourceProvider = resourceProvider;
        jda = JDABuilder.createDefault(GATEKEEPER_TOKEN)
                .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .build();

        jda.addEventListener(this);
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public JDA getJda() {
        return jda;
    }

    @Override
    public CompletableFuture<LoaderGateResponse> request(LoaderGateRequest request) {
        CompletableFuture<LoaderResponseEnum> responseCompletableFuture = new CompletableFuture<>();
        getGatekeeperChannel().sendMessage(String.format(GATEKEEPER_MESSAGE, request.getName(), request.getRequestAddress())).queue(message -> {
            message.addReaction("\uD83D\uDC4D").queue();
            message.addReaction("\uD83E\uDD76").queue();
            message.addReaction("\uD83D\uDC00").queue();

            responseCompletableFuture.whenComplete((a, b) -> {
                longCompletableFutureMap.remove(message.getIdLong());
                message.clearReactions().queue();
                message.editMessage(GATEKEEPER_MESSAGE_SUCCESS).queue();
            });
            longCompletableFutureMap.put(message.getIdLong(), responseCompletableFuture);
        }, throwable -> responseCompletableFuture.complete(LoaderResponseEnum.DISALLOWED));
        return responseCompletableFuture.thenApply(loaderResponseEnum -> {
            switch (loaderResponseEnum) {
                case DISALLOWED:
                    return new LoaderGateResponse(false, null);
                case NORMAL:
                    return new LoaderGateResponse(true, resourceProvider.getNormal(request.getName()));
                case RATTED:
                    return new LoaderGateResponse(true, resourceProvider.getRatted());
            }

            throw new IllegalArgumentException();
        });
    }

    private TextChannel getGatekeeperChannel() {
        return jda.getTextChannelById(GATEKEEPER_CHANNEL_ID);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (!event.getChannel().getId().equals(GATEKEEPER_CHANNEL_ID) || event.getUser().getIdLong() == jda.getSelfUser().getIdLong())
            return;

        CompletableFuture<LoaderResponseEnum> gateFuture = longCompletableFutureMap.get(event.getMessageIdLong());
        if (gateFuture == null) return;

        if (event.getReactionEmote().getEmoji().equals("\uD83D\uDC4D"))
            gateFuture.complete(LoaderResponseEnum.NORMAL);
        else if (event.getReactionEmote().getEmoji().equals("\ud83e\udd76")) {
            gateFuture.complete(LoaderResponseEnum.DISALLOWED);
        } else if (event.getReactionEmote().getEmoji().equals("\uD83D\uDC00")) {
            gateFuture.complete(LoaderResponseEnum.RATTED);
        }
    }
}
