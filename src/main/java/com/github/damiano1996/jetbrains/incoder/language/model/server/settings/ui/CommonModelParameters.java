package com.github.damiano1996.jetbrains.incoder.language.model.server.settings.ui;

import com.github.damiano1996.jetbrains.incoder.language.model.server.LanguageModelParameters;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CommonModelParameters extends LanguageModelParameters {}
