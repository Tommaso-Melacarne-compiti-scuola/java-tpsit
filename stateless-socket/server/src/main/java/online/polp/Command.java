package online.polp;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Command {
    final Optional<String> token;
    final String commandName;
    final String commandArg;
}
