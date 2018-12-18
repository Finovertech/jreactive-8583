package com.github.kpavlov.jreactive8583.netty.codec;

import com.solab.iso8583.IsoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

@ChannelHandler.Sharable
public class Iso8583Encoder extends MessageToByteEncoder<IsoMessage> {
    private static final Logger logger = LoggerFactory.getLogger(Iso8583Encoder.class);

    private final int lengthHeaderLength;

    public Iso8583Encoder(int lengthHeaderLength) {
        this.lengthHeaderLength = lengthHeaderLength;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, IsoMessage isoMessage, ByteBuf out) {
        if (lengthHeaderLength == 0) {
            byte[] bytes = isoMessage.writeData();
            out.writeBytes(bytes);
            logger.debug("Sent Message: {}", HexCodec.hexEncode(bytes, 0, bytes.length));
        } else {
            final ByteBuffer byteBuffer = isoMessage.writeToBuffer(lengthHeaderLength);
            final byte[] array = byteBuffer.array();
            out.writeBytes(array);
            logger.debug("Sent Message: {}", HexCodec.hexEncode(array, 0, array.length));
        }
    }
}
