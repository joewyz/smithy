.. _rules-engine-aws-authscheme-validators:

=================================================
AWS rules engine authentication scheme validators
=================================================

AWS-specific rules engine library :ref:`authentication scheme validators <rules-engine-endpoint-rule-set-endpoint-authschemes>`
make it possible to validate configurations for AWS authentication schemes like
`AWS signature version 4`_ and `AWS signature version 4a`_. An additional
dependency is required to access these validators.

The following example adds ``smithy-aws-endpoints`` as a dependency to a Smithy project:

.. tab:: Smithy CLI

    .. code-block:: json
        :caption: smithy-build.json

        {
            "maven": {
                "dependencies": [
                    "software.amazon.smithy:smithy-aws-endpoints:__smithy_version__"
                ]
            }
        }

.. tab:: Gradle

    .. tab:: Kotlin

        .. code-block:: kotlin
            :caption: build.gradle.kts

            dependencies {
                ...
                implementation("software.amazon.smithy:smithy-aws-endpoints:__smithy_version__")
                ...
            }

    .. tab:: Groovy

        .. code-block:: groovy
            :caption: build.gradle

            dependencies {
                ...
                implementation 'software.amazon.smithy:smithy-aws-endpoints:__smithy_version__'
                ...
            }

.. _rules-engine-aws-authscheme-validator-sigv4:

-----------------------------------------
``sigv4`` authentication scheme validator
-----------------------------------------

Requirement
    The ``name`` property is the string value ``sigv4``.
Properties
    .. list-table::
        :header-rows: 1
        :widths: 10 20 70

        * - Property
          - Type
          - Description
        * - ``signingName``
          - ``option<string>``
          - The "service" value to use when creating a signing string for this
            endpoint.
        * - ``signingRegion``
          - ``option<string>``
          - The "region" value to use when creating a signing string for this
            endpoint.
        * - ``disableDoubleEncoding``
          - ``option<boolean>``
          - Default: ``false``. When ``true``, clients MUST NOT double-escape
            the path during signing.
        * - ``disableNormalizePath``
          - ``option<boolean>``
          - Default: ``false``. When ``true``, clients MUST NOT perform any
            path normalization during signing.


.. _rules-engine-aws-authscheme-validator-sigv4a:

------------------------------------------
``sigv4a`` authentication scheme validator
------------------------------------------

Requirement
    The ``name`` property is the string value ``sigv4a``.
Properties
    .. list-table::
        :header-rows: 1
        :widths: 10 20 70

        * - Property
          - Type
          - Description
        * - ``signingName``
          - ``option<string>``
          - The "service" value to use when creating a signing string for this
            endpoint.
        * - ``signingRegionSet``
          - ``array<string>``
          - The set of signing regions to use when creating a signing string
            for this endpoint.
        * - ``disableDoubleEncoding``
          - ``option<boolean>``
          - Default: ``false``. When ``true``, clients MUST NOT double-escape
            the path during signing.
        * - ``disableNormalizePath``
          - ``option<boolean>``
          - Default: ``false``. When ``true``, clients MUST NOT perform any
            path normalization during signing.


.. _AWS signature version 4: https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html
.. _AWS signature version 4a: https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_sigv.html#how-sigv4a-works
