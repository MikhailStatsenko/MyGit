package com.vcs.vitalitygit.domain.dto.comand;

import java.util.Set;

public record StatusResponse (Set<String> unindexed, Set<String> indexed) {}