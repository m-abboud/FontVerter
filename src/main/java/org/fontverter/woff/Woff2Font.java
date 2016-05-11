package org.fontverter.woff;

import org.fontverter.FontVerterUtils;
import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import java.io.IOException;

public class Woff2Font extends WoffFont {
    public void addFontTable(byte[] data, WoffConstants.TableFlagType flag, long checksum) {
        FontTable table = new Woff2FontTable(data, flag);
        tables.add(table);
    }

    public boolean detectFormat(byte[] fontFile) {
        return FontVerterUtils.bytesStartsWith(fontFile, "wOF2");
    }

    public static class Woff2FontTable extends FontTable{
        public Woff2FontTable(byte[] table, WoffConstants.TableFlagType flag) {
            super(table, flag);
        }

        protected byte[] compress(byte[] bytes) throws IOException {
            BrotliLibraryLoader.loadBrotli();
            BrotliStreamCompressor streamCompressor = new BrotliStreamCompressor(Brotli.DEFAULT_PARAMETER);

            return streamCompressor.compressArray(bytes, true);
        }

        public byte[] getDirectoryData() throws IOException {
            WoffOutputStream writer = new WoffOutputStream();
            writer.writeUnsignedInt8(flag.getValue());
            // tag would be here but spec says optional and it's same as flag
            writer.writeUIntBase128(tableData.length);
            writer.writeUIntBase128(getCompressedTableData().length - paddingAdded);

            return writer.toByteArray();
        }
    }
}
