package lv.agg.service;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import lv.agg.dto.AvailabilitySlotDTO;
import lv.agg.entity.AppointmentEntity;
import lv.agg.entity.AvailabilitySlotEntity;
import lv.agg.repository.AppointmentRepository;
import lv.agg.repository.AvailabilityRepository;
import lv.agg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    public RangeSet<ZonedDateTime> findAvailabiltySlots(Long serviceId, ZonedDateTime from, ZonedDateTime to) {
        List<AvailabilitySlotEntity> availabilitySlots = availabilityRepository.findAvailabilitySlots(serviceId, from, to);
        Map<Long, RangeSet<ZonedDateTime>> userAvailableTime = new HashMap<>();
        availabilitySlots.forEach(e -> userAvailableTime.putIfAbsent(
                e.getUser().getId(),
                TreeRangeSet.create(Collections.singletonList(Range.closed(e.getDateFrom(), e.getDateTo()))))
        );

        subtractAppointments(userAvailableTime, from, to);

        return TreeRangeSet.create(userAvailableTime.values().stream()
                .flatMap(s -> s.asRanges().stream())
                .collect(Collectors.toList()));
    }

    private void subtractAppointments(
            Map<Long, RangeSet<ZonedDateTime>> userAvailableTime,
            ZonedDateTime from,
            ZonedDateTime to
    ) {
        userAvailableTime.forEach((key, value) -> appointmentRepository.findServiceProviderAppointments(key, from, to)
                .stream()
                .filter(e -> e.getStatus() == AppointmentEntity.Status.CONFIRMED)
                .forEach(a -> value.remove(Range.closed(a.getDateFrom(), a.getDateTo()))));
    }

    public void createAvailability(AvailabilitySlotDTO availabilitySlotDTO) {
        //TODO check if user can provide this service           availabilitySlotDTO.getServiceId()
        AvailabilitySlotEntity availabilitySlotEntity = new AvailabilitySlotEntity();
        availabilitySlotEntity.setDateFrom(availabilitySlotDTO.getTimeSlotDTO().getDateFrom());
        availabilitySlotEntity.setDateTo(availabilitySlotDTO.getTimeSlotDTO().getDateTo());
        availabilitySlotEntity.setUser(userRepository.findById(availabilitySlotDTO.getUserId())
                .orElseThrow(RuntimeException::new));
        availabilityRepository.save(availabilitySlotEntity);
    }

    public void updateAvailability(Long id, AvailabilitySlotDTO availabilitySlotDTO) {
        AvailabilitySlotEntity availabilitySlotEntity = availabilityRepository.findById(id)
                .orElseThrow(RuntimeException::new);
        availabilitySlotEntity.setDateFrom(availabilitySlotDTO.getTimeSlotDTO().getDateFrom());
        availabilitySlotEntity.setDateTo(availabilitySlotDTO.getTimeSlotDTO().getDateTo());
        availabilityRepository.save(availabilitySlotEntity);
    }

    public void deleteAvailabitiy(Long id) {
        availabilityRepository.deleteById(id);
    }

}
