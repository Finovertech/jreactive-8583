package com.github.kpavlov.jreactive8583.netty.codec;

import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.util.HexCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.text.ParseException;
import java.util.List;

public class Iso8583Decoder extends ByteToMessageDecoder {

    protected static final Logger logger = getLogger(Iso8583Decoder.class);
    private final MessageFactory messageFactory;

    public Iso8583Decoder(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    /**
     * @implNote Message body starts immediately, no length header,
     * see <code>"lengthFieldFameDecoder"</code> in
     * {@link com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer#initChannel}
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List out) throws Exception {
        if (!byteBuf.isReadable()) {
            return;
        }
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        logger.debug("Message Received: {}", HexCodec.hexEncode(bytes, 0, bytes.length));
        final IsoMessage isoMessage = messageFactory.parseMessage(bytes, 0);
        if (isoMessage != null) {
            //noinspection unchecked
            out.add(isoMessage);
        } else {
            throw new ParseException("Can't parse ISO8583 message", 0);
        }
    }
}
