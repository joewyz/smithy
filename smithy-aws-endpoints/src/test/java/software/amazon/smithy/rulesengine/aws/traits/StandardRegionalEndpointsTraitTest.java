/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package software.amazon.smithy.rulesengine.aws.traits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.ShapeId;

class StandardRegionalEndpointsTraitTest {
    @Test
    public void loadsFromModel() {
        final Model model = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(getClass().getResource("standardRegionalEndpoints.smithy"))
                .assemble()
                .unwrap();
        StandardRegionalEndpointsTrait trait;

        trait = getTraitFromService(model, "ns.foo#Service1");

        assertEquals(trait.getPartitionSpecialCases().size(), 0);
        assertEquals(trait.getRegionSpecialCases().size(), 0);
        assertEquals(trait,
                new StandardRegionalEndpointsTrait.Provider().createTrait(StandardRegionalEndpointsTrait.ID,
                        trait.toBuilder().build().toNode()));

        trait = getTraitFromService(model, "ns.foo#Service2");

        assertEquals(trait.getPartitionSpecialCases().size(), 0);
        assertEquals(trait.getRegionSpecialCases().size(), 0);
        assertEquals(trait,
                new StandardRegionalEndpointsTrait.Provider().createTrait(StandardRegionalEndpointsTrait.ID,
                        trait.toBuilder().build().toNode()));

        trait = getTraitFromService(model, "ns.foo#Service3");

        assertEquals(trait.getPartitionSpecialCases().size(), 1);
        assertEquals(trait.getRegionSpecialCases().size(), 1);
        List<PartitionSpecialCase> partitionSpecialCases = trait.getPartitionSpecialCases().get("aws-us-gov");

        PartitionSpecialCase partitionSpecialCase1 = partitionSpecialCases.get(0);
        assertEquals(partitionSpecialCase1.getEndpoint(), "https://myservice.{region}.{dnsSuffix}");
        assertEquals(partitionSpecialCase1.getFips(), true);
        assertNull(partitionSpecialCase1.getDualStack());

        PartitionSpecialCase partitionSpecialCase2 = partitionSpecialCases.get(1);
        assertEquals(partitionSpecialCase2.getEndpoint(), "https://myservice.global.amazonaws.com");
        assertEquals(partitionSpecialCase2.getDualStack(), true);
        assertNull(partitionSpecialCase2.getFips());

        List<RegionSpecialCase> regionSpecialCases = trait.getRegionSpecialCases().get("us-east-1");
        assertEquals(regionSpecialCases.size(), 1);
        RegionSpecialCase regionSpecialCase = regionSpecialCases.get(0);
        assertEquals(regionSpecialCase.getEndpoint(), "https://myservice.amazonaws.com");
        assertEquals(regionSpecialCase.getDualStack(), true);
        assertEquals(trait,
                new StandardRegionalEndpointsTrait.Provider().createTrait(StandardRegionalEndpointsTrait.ID,
                        trait.toBuilder().build().toNode()));

        Node.assertEquals(trait.toNode(), trait.toBuilder().build().toNode());
    }

    private StandardRegionalEndpointsTrait getTraitFromService(Model model, String service) {
        return model
                .expectShape(ShapeId.from(service))
                .asServiceShape()
                .get()
                .getTrait(StandardRegionalEndpointsTrait.class)
                .get();
    }
}
