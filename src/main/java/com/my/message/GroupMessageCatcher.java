package com.my.message;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.*;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupMessageCatcher {
    public static SimpleListenerHost getListener() {
        List<GroupMessageHandler> handlerList = new ArrayList<>();
        //handlerList.add(new SimpleMsgHandler());
        handlerList.add(new ParameterHandler());
        //handlerList.add(new InfoMessageHandler());
        handlerList.add(new DefendHandler());
        handlerList.add(new RequireImageHandler());
        handlerList.add(new QueryImageHandler());
        handlerList.add(new RankHandler());
        //handlerList.add(new ImageSearchHandler());

        //RepeatMessage repeat = new RepeatMessage();

        SimpleListenerHost host = new SimpleListenerHost() {
            @EventHandler
            public ListeningStatus onGroupMessage(GroupMessageEvent event) {
                MessageChain chain = event.getMessage();
                MessageChain content = MessageTool.removeSource(chain);
                /*
                if (repeat.put(content)) {
                    event.getGroup().sendMessage(content);
                }*/
                for (GroupMessageHandler handler: handlerList) {
                    if (handler.accept(chain, event.getSender())) {
                        MessageChain reply = handler.reply();
                        if (reply != null) {
                            if (handler.needQuote) {
                                event.getGroup().sendMessage(MessageTool.quoteMsg(event, reply));
                            } else {
                                event.getGroup().sendMessage(reply);
                            }
                        }
                        break;
                    }
                }

                return ListeningStatus.LISTENING;
            }

            @EventHandler
            public ListeningStatus onFriendMessageEvent(FriendMessageEvent event) {
                MessageChain chain = event.getMessage();
                event.getFriend().sendMessage(chain);
                return ListeningStatus.LISTENING;
            }

            @Override
            public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
                super.handleException(context, exception);
            }
        };
        return host;
    }
}
