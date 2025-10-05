package org.dspace.app.rest.diracai.util;

import org.dspace.content.Bitstream;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.core.Context;
import org.dspace.content.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class MetadataUtils {

    @Autowired
    private ItemService itemService;

    private static final String METADATA_SCHEMA = "dc";
    private static final String METADATA_ELEMENT = "date";
    private static final String METADATA_QUALIFIER = "disposal";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Date getDisposalDate(Context context, Bitstream bitstream) {
        try {
            // Get the owning item of the bitstream
            Item owningItem = bitstream.getBundles().stream()
                    .flatMap(bundle -> {
                        return bundle.getItems().stream();
                    })
                    .findFirst()
                    .orElse(null);

            if (owningItem == null) {
                return null;
            }

            // Use itemService to fetch metadata
            List<MetadataValue> metadataValues = itemService.getMetadata(
                    owningItem, METADATA_SCHEMA, METADATA_ELEMENT, METADATA_QUALIFIER, null);

            if (metadataValues == null || metadataValues.isEmpty()) {
                return null;
            }

            String dateStr = metadataValues.get(0).getValue();
            if (dateStr == null || dateStr.isBlank()) {
                return null;
            }

            return dateFormat.parse(dateStr);
        } catch (SQLException | ParseException e) {
            throw new RuntimeException("Failed to retrieve or parse dc.disposal.date metadata", e);
        }
    }
}
