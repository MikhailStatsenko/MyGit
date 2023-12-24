package com.vcs.mygit.git.dto.response;

import java.util.Set;

public record StatusResponse (Set<String> unindexed, Set<String> indexed) {}