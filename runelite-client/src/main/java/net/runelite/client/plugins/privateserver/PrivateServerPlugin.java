/*
 * Copyright (c) 2019, xperiaclash <https://github.com/xperiaclash>
 * Copyright (c) 2019, ganom <https://github.com/Ganom>
 * Copyright (c) 2019, gazivodag <https://github.com/gazivodag>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.privateserver;

import com.google.inject.Provides;
import java.math.BigInteger;
import java.text.NumberFormat;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
        name = "Private Server",
        description = "Settings for connecting to non official servers",
        tags = {"RSPS", "Server", "Private"},
        enabledByDefault = true
)
@Singleton
@Slf4j
public class PrivateServerPlugin extends Plugin
{
    @Inject
    private Client client;
    @Inject
    private PrivateServerConfig config;
    @Inject
    private EventBus eventBus;

    @Provides
    PrivateServerConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(PrivateServerConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        updateConfig();
        addSubscriptions();
    }

    @Override
    protected void shutDown() throws Exception
    {
        eventBus.unregister(this);
    }

    private void addSubscriptions()
    {
        //  eventBus.register(ConfigChanged.class, this, onConfigChanged());
    }

    private void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals("privateserver") && event.getKey().equals("modulus"))
        {
            client.setModulus(new BigInteger(config.modulus(), 16));
        }

        if (event.getGroup().equals("privateserver") && event.getKey().equals("codebase"))
        {
            String message = "Client restart required after codebase change\n";
            JOptionPane.showMessageDialog(new JFrame(), message, "Restart required",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateConfig()
    {
        try {
            client.setModulus(new BigInteger(config.modulus(), 16));
        } catch (NumberFormatException e) {
            String message = "RSA Key is invalid. Go to Private Server Plugin Settings and fix it. \n";
            JOptionPane.showMessageDialog(new JFrame(), message, "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

}