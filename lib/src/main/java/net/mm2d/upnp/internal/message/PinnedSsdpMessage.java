/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.upnp.internal.message;

import net.mm2d.upnp.SsdpMessage;

import java.io.OutputStream;
import java.net.InetAddress;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 固定デバイス用のSsdpMessage
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public class PinnedSsdpMessage implements SsdpMessage {
    @Nonnull
    private final String mLocation;
    @Nonnull
    private String mUuid = "";
    @Nullable
    private InetAddress mLocalAddress;

    public PinnedSsdpMessage(@Nonnull final String location) {
        mLocation = location;
    }

    @Override
    public int getScopeId() {
        return 0;
    }

    public void setLocalAddress(@Nonnull final InetAddress address) {
        mLocalAddress = address;
    }

    @Nullable
    @Override
    public InetAddress getLocalAddress() {
        return mLocalAddress;
    }

    @Nullable
    @Override
    public String getHeader(@Nonnull final String name) {
        return null;
    }

    @Override
    public void setHeader(
            @Nonnull final String name,
            @Nonnull final String value) {
    }

    public void setUuid(@Nonnull final String uuid) {
        mUuid = uuid;
    }

    @Nonnull
    @Override
    public String getUuid() {
        return mUuid;
    }

    @Nonnull
    @Override
    public String getType() {
        return "";
    }

    @Nullable
    @Override
    public String getNts() {
        return SSDP_ALIVE;
    }

    @Override
    public int getMaxAge() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long getExpireTime() {
        return Long.MAX_VALUE;
    }

    @Nonnull
    @Override
    public String getLocation() {
        return mLocation;
    }

    @Override
    public void writeData(@Nonnull final OutputStream os) {
    }
}
