/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package software.amazon.smithy.diff.evaluators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

import java.util.List;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.diff.ModelDiff;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.shapes.OperationShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.traits.ErrorTrait;
import software.amazon.smithy.model.validation.ValidationEvent;

public class RemovedOperationErrorTest {
    @Test
    public void detectsRemovedErrors() {
        SourceLocation source = new SourceLocation("foo.smithy");
        Shape e1 = StructureShape.builder()
                .id("foo.baz#E1")
                .addTrait(new ErrorTrait("client"))
                .build();
        Shape e2 = StructureShape.builder()
                .id("foo.baz#E2")
                .addTrait(new ErrorTrait("client"))
                .build();
        OperationShape operation1 = OperationShape.builder()
                .id("foo.baz#Operation")
                .addError(e1)
                .addError(e2)
                .source(source)
                .build();
        Shape operation2 = operation1.toBuilder().clearErrors().build();
        Model modelA = Model.assembler().addShapes(operation1, e1, e2).assemble().unwrap();
        Model modelB = Model.assembler().addShapes(operation2, e1, e2).assemble().unwrap();
        List<ValidationEvent> events = ModelDiff.compare(modelA, modelB);

        // Emits an event for each removal.
        assertThat(TestHelper.findEvents(events, "RemovedOperationError").size(), equalTo(2));
        assertThat(events.stream().filter(e -> e.getSourceLocation().equals(source)).count(), equalTo(2L));
        assertThat(TestHelper.findEvents(events, "RemovedOperationError.E1").size(), equalTo(1));
        assertThat(TestHelper.findEvents(events, "RemovedOperationError.E2").size(), equalTo(1));
        assertThat(TestHelper.findEvents(events, "RemovedOperationError").get(0).toString(),
                startsWith("[WARNING] foo.baz#Operation: The `foo.baz#E1` error was removed " +
                        "from the `foo.baz#Operation` operation. | RemovedOperationError"));
        assertThat(TestHelper.findEvents(events, "RemovedOperationError").get(1).toString(),
                startsWith("[WARNING] foo.baz#Operation: The `foo.baz#E2` error was removed " +
                        "from the `foo.baz#Operation` operation. | RemovedOperationError"));
    }
}
