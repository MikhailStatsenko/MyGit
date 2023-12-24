package com.vcs.mygit.git.dto.response;

import java.util.Set;

public record StatusResponse (Set<String> untracked, Set<String> modified) {}