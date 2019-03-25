package lv.agg.service;

import lv.agg.configuration.AggregatorAppPrincipal;
import lv.agg.dto.AppointmentDTO;
import lv.agg.entity.AppointmentEntity;
import lv.agg.entity.ServiceEntity;
import lv.agg.entity.UserEntity;
import lv.agg.repository.AppointmentRepository;
import lv.agg.repository.ServiceRepository;
import lv.agg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private UserRepository userRepository;

    public List<AppointmentDTO> searchCustomerAppointments(ZonedDateTime dateFrom, ZonedDateTime dateTo) {
        Long customerId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return appointmentRepository.findCustomerAppointments(customerId, dateFrom, dateTo).stream()
                .map(this::toAppointmentDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> searchServiceProviderAppointments(ZonedDateTime dateFrom, ZonedDateTime dateTo) {
        Long serviceProviderId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return appointmentRepository.findServiceProviderAppointments(serviceProviderId, dateFrom, dateTo)
                .stream()
                .map(this::toAppointmentDTO)
                .collect(Collectors.toList());
    }

    //TODO separate mapping
    private AppointmentDTO toAppointmentDTO(AppointmentEntity e) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setFrom(e.getDateFrom());
        dto.setTo(e.getDateTo());
        dto.setServiceId(e.getService().getId());
        dto.setServiceProviderId(e.getServiceProvider().getId());
        dto.setStatus(e.getStatus().name());
        return dto;
    }

    public Long createAppointment(AppointmentDTO appointmentDTO) {
        Long customerId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        UserEntity consumer = userRepository.findById(customerId)
                .orElseThrow(RuntimeException::new);
        ServiceEntity serviceEntity = serviceRepository.findById(appointmentDTO.getServiceId())
                .orElseThrow(RuntimeException::new);
        UserEntity serviceProvider = userRepository.findById(appointmentDTO.getServiceProviderId())
                .orElseThrow(RuntimeException::new);
        List<AppointmentEntity> userAppointments = appointmentRepository.findClashingAppointments(serviceProvider.getId(),
                appointmentDTO.getFrom(), appointmentDTO.getTo());
        if (userAppointments.stream().anyMatch(e -> e.getStatus() == AppointmentEntity.Status.CONFIRMED)) {
            throw new RuntimeException();
        }
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setDateFrom(appointmentDTO.getFrom());
        appointmentEntity.setDateTo(appointmentDTO.getTo());
        appointmentEntity.setService(serviceEntity);
        appointmentEntity.setServiceProvider(serviceProvider);
        appointmentEntity.setCustomer(consumer);
        appointmentEntity.setStatus(AppointmentEntity.Status.NEW);
        appointmentEntity = appointmentRepository.save(appointmentEntity);

        return appointmentEntity.getId();
    }

    public void confirmAppointment(Long appointmentId) {
        Long serviceProviderId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        AppointmentEntity appointmentEntity = appointmentRepository.findNewAppointment(appointmentId, serviceProviderId,
                AppointmentEntity.Status.NEW)
                .orElseThrow(RuntimeException::new);
        appointmentEntity.setStatus(AppointmentEntity.Status.CONFIRMED);
        appointmentRepository.save(appointmentEntity);
    }

    public AppointmentDTO getAppointmentById(Long appointmentId) {
        AppointmentEntity appointmentEntity = appointmentRepository.findById(appointmentId)
                .orElseThrow(RuntimeException::new);
        return toAppointmentDTO(appointmentEntity);
    }

    public void updateApppointment(Long id, AppointmentDTO appointmentDTO) {
        AppointmentEntity appointmentEntity = appointmentRepository.findById(id).orElseThrow(RuntimeException::new);
        appointmentEntity.setDateFrom(appointmentDTO.getFrom());
        appointmentEntity.setDateTo(appointmentDTO.getTo());
        appointmentRepository.save(appointmentEntity);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

}
