/*
 * Copyright (c) 2019 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at:
 *
 *     https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.jkube.kit.config.resource;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class IngressConfigTest {
  /**
   * Verifies that deserialization works for raw deserialization (Maven-Plexus).
   */
  @Test
  void rawDeserialization() throws IOException {
    // Given
    final JsonMapper mapper = JsonMapper.builder()
        .configure(MapperFeature.USE_ANNOTATIONS, false)
        .build();
    // When
    final IngressConfig result = mapper.readValue(
        IngressConfigTest.class.getResourceAsStream("/ingress-config.json"),
        IngressConfig.class);
    // Then
    assertThat(result)
        .satisfies(ic -> assertThat(ic).extracting(IngressConfig::getIngressRules)
            .asInstanceOf(InstanceOfAssertFactories.list(IngressRuleConfig.class))
            .containsExactly(
                IngressRuleConfig.builder()
                    .host("example.com")
                    .path(IngressRulePathConfig.builder()
                        .pathType("ImplementationSpecific")
                        .path("/path")
                        .serviceName("service-name")
                        .servicePort(8080)
                        .resource(IngressRulePathResourceConfig.builder()
                            .apiGroup("group.k8s.io")
                            .kind("ResourceKind")
                            .name("resource-name")
                            .build())
                        .build())
                    .build()))
        .satisfies(ic -> assertThat(ic).extracting(IngressConfig::getIngressTlsConfigs)
            .asInstanceOf(InstanceOfAssertFactories.list(IngressTlsConfig.class))
            .singleElement()
            .hasFieldOrPropertyWithValue("secretName", "shhhh")
            .extracting("hosts")
            .asInstanceOf(InstanceOfAssertFactories.list(String.class))
            .containsExactly("tls.example.com"));
  }
}