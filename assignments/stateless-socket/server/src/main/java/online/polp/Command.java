package online.polp;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Command {
    @Builder.Default
    Optional<String> token = Optional.empty();
    final String commandName;
    final String commandArg;
}
