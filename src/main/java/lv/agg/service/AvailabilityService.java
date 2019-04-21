package lv.agg.service;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import lv.agg.configuration.AggregatorAppPrincipal;
import lv.agg.dto.AvailabilitySlotDTO;
import lv.agg.entity.AppointmentEntity;
import lv.agg.entity.AvailabilitySlotEntity;
import lv.agg.enums.AppointmentStatus;
import lv.agg.repository.AppointmentRepository;
import lv.agg.repository.AvailabilityRepository;
import lv.agg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
        userAvailableTime.forEach((key, value) -> appointmentRepository.findMerchantAppointments(key, from, to)
                .stream()
                .filter(e -> e.getStatus() == AppointmentStatus.CONFIRMED)
                .forEach(a -> value.remove(Range.closed(a.getDateFrom(), a.getDateTo()))));
    }

    public void createAvailability(AvailabilitySlotDTO availabilitySlotDTO) {
        //TODO check if user can provide this service           availabilitySlotDTO.getServiceId
        //TODO check clashing availability slots
        AvailabilitySlotEntity availabilitySlotEntity = new AvailabilitySlotEntity();
        availabilitySlotEntity.setDateFrom(availabilitySlotDTO.getTimeSlotDTO().getDateFrom());
        availabilitySlotEntity.setDateTo(availabilitySlotDTO.getTimeSlotDTO().getDateTo());
        availabilitySlotEntity.setUser(userRepository.findById(availabilitySlotDTO.getUserId())
                .orElseThrow(RuntimeException::new));
        availabilityRepository.save(availabilitySlotEntity);
    }

    public void updateAvailability(Long id, AvailabilitySlotDTO availabilitySlotDTO) {
        AvailabilitySlotEntity slot = availabilityRepository.findById(id)
                .orElseThrow(RuntimeException::new);
        if (availabilitySlotDTO.getTimeSlotDTO().getDateFrom().isAfter(slot.getDateFrom())) {
            ensureNoClashingAppointments(slot.getDateFrom(), availabilitySlotDTO.getTimeSlotDTO().getDateFrom());
        }
        if (availabilitySlotDTO.getTimeSlotDTO().getDateTo().isBefore(slot.getDateTo())) {
            ensureNoClashingAppointments(availabilitySlotDTO.getTimeSlotDTO().getDateTo(), slot.getDateTo());
        }
        slot.setDateFrom(availabilitySlotDTO.getTimeSlotDTO().getDateFrom());
        slot.setDateTo(availabilitySlotDTO.getTimeSlotDTO().getDateTo());
        availabilityRepository.save(slot);
    }

    public void deleteAvailabitiy(Long id) {
        AvailabilitySlotEntity slot = availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Availability slot not found"));
        ensureNoClashingAppointments(slot.getDateFrom(), slot.getDateTo());
        availabilityRepository.deleteById(id);
    }

    private void ensureNoClashingAppointments(ZonedDateTime dateFrom, ZonedDateTime dateTo) {
        Long merchantId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        List<AppointmentEntity> clashingAppointments = appointmentRepository.findClashingMerchantAppointments(merchantId,
                AppointmentStatus.CONFIRMED, dateFrom, dateTo);
        if (!CollectionUtils.isEmpty(clashingAppointments)) {
            throw new RuntimeException("Clashing appointment found");
        }
    }

}
