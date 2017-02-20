/*
 * Copyright(C)  2017 大前良介(OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.upnp;

import net.mm2d.util.TestUtils;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InterfaceAddress;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(Enclosed.class)
public class SsdpRequestMessageTest {

    private static SsdpRequestMessage makeFromResource(String name) throws IOException {
        final byte[] data = TestUtils.getResourceData(name);
        return new SsdpRequestMessage(mock(InterfaceAddress.class), data, data.length);
    }

    @RunWith(JUnit4.class)
    public static class 作成 {
        @Test
        public void buildUp_所望のバイナリに変換できる() throws IOException {
            final SsdpRequestMessage message = new SsdpRequestMessage();
            message.setMethod(SsdpMessage.M_SEARCH);
            message.setUri("*");
            message.setHeader(Http.HOST, SsdpServer.SSDP_ADDR + ":" + String.valueOf(SsdpServer.SSDP_PORT));
            message.setHeader(Http.MAN, SsdpMessage.SSDP_DISCOVER);
            message.setHeader(Http.MX, "1");
            message.setHeader(Http.ST, SsdpSearchServer.ST_ROOTDEVICE);

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            message.getMessage().writeData(baos);
            final byte[] actual = baos.toByteArray();

            final byte[] expected = TestUtils.getResourceData("ssdp-search-request.bin");

            assertThat(new String(actual), is(new String(expected)));
        }

        @Test
        public void buildUp_受信データから作成() throws IOException {
            final SsdpRequestMessage message = makeFromResource("ssdp-notify-alive0.bin");

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            message.getMessage().writeData(baos);
            final byte[] actual = baos.toByteArray();

            final byte[] expected = TestUtils.getResourceData("ssdp-notify-alive0.bin");

            assertThat(new String(actual), is(new String(expected)));
        }
    }

    @RunWith(JUnit4.class)
    public static class 個別パラメータ {
        @Test
        public void getType() throws IOException {
            final SsdpRequestMessage message = makeFromResource("ssdp-notify-alive2.bin");

            assertThat(message.getType(), is("urn:schemas-upnp-org:service:ContentDirectory:1"));

        }
    }

    @RunWith(Theories.class)
    public static class Notifyメッセージ {
        @DataPoints
        public static SsdpRequestMessage[] getMessages() throws IOException {
            return new SsdpRequestMessage[]{
                    makeFromResource("ssdp-notify-alive0.bin"),
                    makeFromResource("ssdp-notify-alive1.bin"),
                    makeFromResource("ssdp-notify-alive2.bin"),
                    makeFromResource("ssdp-notify-byebye0.bin"),
                    makeFromResource("ssdp-notify-byebye1.bin"),
                    makeFromResource("ssdp-notify-byebye2.bin"),
            };
        }

        @Theory
        public void getMethod_NOTIFYであること(SsdpRequestMessage message) {
            assertThat(message.getMethod(), is(SsdpMessage.NOTIFY));
        }

        @Theory
        public void getUri_アスタリスクであること(SsdpRequestMessage message) {
            assertThat(message.getUri(), is("*"));
        }

        @Theory
        public void getUuid_記述の値であること(SsdpRequestMessage message) {
            assertThat(message.getUuid(), is("uuid:11111111-1111-1111-1111-111111111111"));
        }

        @Theory
        public void getHeader_HOST_SSDPのアドレスであること(SsdpRequestMessage message) {
            assertThat(message.getHeader(Http.HOST), is(SsdpServer.SSDP_ADDR + ":" + String.valueOf(SsdpServer.SSDP_PORT)));
        }
    }


    @RunWith(Theories.class)
    public static class Aliveメッセージ {
        @DataPoints
        public static SsdpRequestMessage[] getMessages() throws IOException {
            return new SsdpRequestMessage[]{
                    makeFromResource("ssdp-notify-alive0.bin"),
                    makeFromResource("ssdp-notify-alive1.bin"),
                    makeFromResource("ssdp-notify-alive2.bin"),
            };
        }

        @Theory
        public void getNts_NTSがAliveであること(SsdpRequestMessage message) {
            assertThat(message.getNts(), is(SsdpMessage.SSDP_ALIVE));
        }

        @Theory
        public void getMaxAge_CACHE_CONTROLの値が取れること(SsdpRequestMessage message) {
            assertThat(message.getMaxAge(), is(300));
        }

        @Theory
        public void getLocation_Locationの値が取れること(SsdpRequestMessage message) {
            assertThat(message.getLocation(), is("http://192.0.2.2:12345/description.xml"));
        }
    }

    @RunWith(Theories.class)
    public static class ByeByeメッセージ {
        @DataPoints
        public static SsdpRequestMessage[] getMessages() throws IOException {
            return new SsdpRequestMessage[]{
                    makeFromResource("ssdp-notify-byebye0.bin"),
                    makeFromResource("ssdp-notify-byebye1.bin"),
                    makeFromResource("ssdp-notify-byebye2.bin"),
            };
        }

        @Theory
        public void getNts_NTSがByebyeであること(SsdpRequestMessage message) {
            assertThat(message.getNts(), is(SsdpMessage.SSDP_BYEBYE));
        }
    }
}