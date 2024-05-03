package com.vcs.vitalitygit.git.dto.response;

import java.util.List;

public record DeleteFileResponse (List<String> deletedFiles) {
}
