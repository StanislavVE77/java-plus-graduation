package ru.yandex.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.request.dto.RequestEventDto;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EventResultRequestStatusDto {

    private List<RequestEventDto> confirmedRequests;

    private List<RequestEventDto> rejectedRequests;
}
