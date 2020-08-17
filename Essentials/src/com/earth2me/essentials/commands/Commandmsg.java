package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.User;
import com.earth2me.essentials.messaging.IMessageRecipient;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.FormatUtil;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import static com.earth2me.essentials.I18n.tl;

public class Commandmsg extends EssentialsLoopCommand
{
    public Commandmsg()
    {
        super("msg");
    }

    @Override
    public void run(Server server, CommandSource sender, String commandLabel, String[] args) throws Exception
    {
        if (args.length < 2)
        {
            throw new NotEnoughArgumentsException();
        }

        String message = getFinalArg(args, 1);
        boolean canWildcard = getTFMHandler().isStaff(sender.getPlayer());
        if (sender.isPlayer())
        {
            User user = ess.getUser(sender.getPlayer());
            if (user.isMuted())
            {
                String dateDiff = user.getMuteTimeout() > 0 ? DateUtil.formatDateDiff(user.getMuteTimeout()) : null;
                if (dateDiff == null)
                {
                    throw new Exception(user.hasMuteReason() ? tl("voiceSilencedReason", user.getMuteReason()) : tl("voiceSilenced"));
                }
                throw new Exception(user.hasMuteReason() ? tl("voiceSilencedReasonTime", dateDiff, user.getMuteReason()) : tl("voiceSilencedTime", dateDiff));
            }
            message = FormatUtil.formatMessage(user, "essentials.msg", message);
        }
        else
        {
            message = FormatUtil.replaceFormat(message);
        }

        // Sending messages to console
        if (args[0].equalsIgnoreCase(Console.NAME))
        {
            IMessageRecipient messageSender = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : Console.getInstance();
            messageSender.sendMessage(Console.getInstance(), message);
            return;
        }

        loopOnlinePlayers(server, sender, false, canWildcard, args[0], new String[]{message});
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User messageReceiver, final String[] args)
    {
        IMessageRecipient messageSender = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : Console.getInstance();
        if (getTFMHandler().isVanished(messageReceiver) && !getTFMHandler().isStaff(sender.getSender()))
        {
            messageSender.sendMessage(ChatColor.RED + "Error: " + ChatColor.RED + "Player not found.");
            return;
        }
        messageSender.sendMessage(messageReceiver, args[0]); // args[0] is the message.
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args)
    {
        if (args.length == 1)
        {
            return getPlayers(server, sender);
        }
        else
        {
            return null;  // It's a chat message, use the default chat handler
        }
    }
}
