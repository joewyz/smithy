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
import software.amazon.smithy.model.shapes.ShapeId;

class StandardPartitionalEndpointsTraitTest {
    @Test
    public void loadsFromModel() {
        final Model model = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(getClass().getResource("standardPartitionalEndpoints.smithy"))
                .assemble()
                .unwrap();

        StandardPartitionalEndpointsTrait trait;

        trait = getTraitFromService(model, "ns.foo#Service1");

        assertEquals(trait.getEndpointPatternType(), EndpointPatternType.SERVICE_DNSSUFFIX);
        assertEquals(trait.getPartitionEndpointSpecialCases().size(), 0);
        assertEquals(trait,
                new StandardPartitionalEndpointsTrait.Provider()
                        .createTrait(StandardPartitionalEndpointsTrait.ID,
                                trait.toBuilder().build().toNode()));

        trait = getTraitFromService(model, "ns.foo#Service2");

        assertEquals(trait.getEndpointPatternType(), EndpointPatternType.SERVICE_REGION_DNSSUFFIX);
        assertEquals(trait.getPartitionEndpointSpecialCases().size(), 1);

        List<PartitionEndpointSpecialCase> cases = trait.getPartitionEndpointSpecialCases().get("aws-us-gov");

        PartitionEndpointSpecialCase case1 = cases.get(0);
        assertEquals(case1.getEndpoint(), "https://myservice.{region}.{dnsSuffix}");
        assertEquals(case1.getFips(), true);
        assertEquals(case1.getRegion(), "us-east-1");
        assertNull(case1.getDualStack());

        PartitionEndpointSpecialCase case2 = cases.get(1);
        assertEquals(case2.getEndpoint(), "https://myservice.global.amazonaws.com");
        assertEquals(case2.getDualStack(), true);
        assertEquals(case2.getRegion(), "us-west-2");
        assertNull(case2.getFips());

        assertEquals(trait,
                new StandardPartitionalEndpointsTrait.Provider()
                        .createTrait(StandardPartitionalEndpointsTrait.ID,
                                trait.toBuilder().build().toNode()));

        trait = getTraitFromService(model, "ns.foo#Service3");

        assertEquals(trait.getEndpointPatternType(), EndpointPatternType.AWS_RECOMMENDED);
        assertEquals(trait.getPartitionEndpointSpecialCases().size(), 1);

        cases = trait.getPartitionEndpointSpecialCases().get("aws");

        case1 = cases.get(0);
        assertEquals(case1.getEndpoint(), "https://myservice.{dnsSuffix}");
        assertEquals(case1.getRegion(), "us-west-2");
        assertNull(case1.getDualStack());
        assertNull(case1.getFips());

        assertEquals(trait,
                new StandardPartitionalEndpointsTrait.Provider()
                        .createTrait(StandardPartitionalEndpointsTrait.ID,
                                trait.toBuilder().build().toNode()));
    }

    private StandardPartitionalEndpointsTrait getTraitFromService(Model model, String service) {
        return model
                .expectShape(ShapeId.from(service))
                .asServiceShape()
                .get()
                .getTrait(StandardPartitionalEndpointsTrait.class)
                .get();
    }
}
