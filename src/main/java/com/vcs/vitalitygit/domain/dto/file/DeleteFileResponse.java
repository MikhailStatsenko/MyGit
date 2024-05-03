package com.vcs.vitalitygit.domain.dto.file;

import java.util.List;

public record DeleteFileResponse (List<String> deletedFiles) {
}
