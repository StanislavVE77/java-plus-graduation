package ru.yandex.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.request.model.RequestStatus;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateRequestStatusDto {

    private Set<Long> requestIds;

    private RequestStatus status;

}
