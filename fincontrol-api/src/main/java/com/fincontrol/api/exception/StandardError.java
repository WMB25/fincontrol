package com.fincontrol.api.exception;

import java.time.LocalDateTime;

public record StandardError(int status, String error, String message, LocalDateTime timestamp) {}