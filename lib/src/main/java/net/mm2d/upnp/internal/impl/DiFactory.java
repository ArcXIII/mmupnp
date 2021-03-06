/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.upnp.internal.impl;

import net.mm2d.upnp.ControlPoint.NotifyEventListener;
import net.mm2d.upnp.Protocol;
import net.mm2d.upnp.TaskExecutor;
import net.mm2d.upnp.internal.manager.DeviceHolder;
import net.mm2d.upnp.internal.manager.DeviceHolder.ExpireListener;
import net.mm2d.upnp.internal.manager.SubscribeHolder;
import net.mm2d.upnp.internal.manager.SubscribeManager;
import net.mm2d.upnp.internal.server.EventReceiver;
import net.mm2d.upnp.internal.server.EventReceiver.EventMessageListener;
import net.mm2d.upnp.internal.server.SsdpNotifyReceiver.NotifyListener;
import net.mm2d.upnp.internal.server.SsdpNotifyReceiverList;
import net.mm2d.upnp.internal.server.SsdpSearchServer.ResponseListener;
import net.mm2d.upnp.internal.server.SsdpSearchServerList;
import net.mm2d.upnp.internal.thread.TaskHandler;

import java.net.NetworkInterface;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ControlPointのテストを容易にするためのDependency injection
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public class DiFactory {
    @Nonnull
    private final Protocol mProtocol;
    @Nullable
    private final TaskExecutor mCallbackExecutor;
    @Nullable
    private final TaskExecutor mIoExecutor;

    public DiFactory() {
        this(Protocol.DEFAULT, null, null);
    }

    DiFactory(@Nonnull final Protocol protocol) {
        this(protocol, null, null);
    }

    public DiFactory(
            @Nonnull final Protocol protocol,
            @Nullable final TaskExecutor callback,
            @Nullable final TaskExecutor io) {
        mProtocol = protocol;
        mCallbackExecutor = callback;
        mIoExecutor = io;
    }

    @Nonnull
    public Map<String, DeviceImpl.Builder> createLoadingDeviceMap() {
        return new HashMap<>();
    }

    @Nonnull
    public DeviceHolder createDeviceHolder(@Nonnull final ExpireListener listener) {
        return new DeviceHolder(listener);
    }

    @Nonnull
    public SsdpSearchServerList createSsdpSearchServerList(
            @Nonnull final Collection<NetworkInterface> interfaces,
            @Nonnull final ResponseListener listener) {
        return new SsdpSearchServerList().init(mProtocol, interfaces, listener);
    }

    @Nonnull
    public SsdpNotifyReceiverList createSsdpNotifyReceiverList(
            @Nonnull final Collection<NetworkInterface> interfaces,
            @Nonnull final NotifyListener listener) {
        return new SsdpNotifyReceiverList().init(mProtocol, interfaces, listener);
    }

    @Nonnull
    public SubscribeManager createSubscribeManager(
            @Nonnull final TaskHandler taskHandler,
            @Nonnull final NotifyEventListener listener) {
        return new SubscribeManager(taskHandler, listener, this);
    }

    @Nonnull
    public SubscribeHolder createSubscribeHolder() {
        return new SubscribeHolder();
    }

    @Nonnull
    public EventReceiver createEventReceiver(@Nonnull final EventMessageListener listener) {
        return new EventReceiver(listener);
    }

    @Nonnull
    public TaskHandler createTaskHandler() {
        return new TaskHandler(mCallbackExecutor, mIoExecutor);
    }
}
