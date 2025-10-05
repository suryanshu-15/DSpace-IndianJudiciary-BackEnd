package org.dspace.app.rest.diracai.service;

import org.dspace.content.Bitstream;
import org.dspace.core.Context;

public interface PdfAConversionService {
    boolean isConvertedToPdfA(Context context, Bitstream bitstream);
    void convertToPdfA(Context context, Bitstream bitstream);
}
