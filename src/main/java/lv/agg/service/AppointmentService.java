package lv.agg.service;

import lv.agg.configuration.AggregatorAppPrincipal;
import lv.agg.dto.AppointmentDTO;
import lv.agg.dto.mapping.AppointmentDTOMapper;
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
                .map(AppointmentDTOMapper::map)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> searchMerchantAppointments(ZonedDateTime dateFrom, ZonedDateTime dateTo) {
        Long merchantId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return appointmentRepository.findMerchantAppointments(merchantId, dateFrom, dateTo)
                .stream()
                .map(AppointmentDTOMapper::map)
                .collect(Collectors.toList());
    }

    public Long createAppointment(AppointmentDTO appointmentDTO) {
        Long customerId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        UserEntity consumer = userRepository.findById(customerId)
                .orElseThrow(RuntimeException::new);
        ServiceEntity serviceEntity = serviceRepository.findById(appointmentDTO.getServiceId())
                .orElseThrow(RuntimeException::new);
        UserEntity merchant = userRepository.findById(appointmentDTO.getMerchantId())
                .orElseThrow(RuntimeException::new);
        List<AppointmentEntity> userAppointments = appointmentRepository.findClashingMerchantAppointments(merchant.getId(),
                AppointmentEntity.Status.CONFIRMED, appointmentDTO.getFrom(), appointmentDTO.getTo());
        if (userAppointments.stream().anyMatch(e -> e.getStatus() == AppointmentEntity.Status.CONFIRMED)) {
            throw new RuntimeException();
        }
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setDateFrom(appointmentDTO.getFrom());
        appointmentEntity.setDateTo(appointmentDTO.getTo());
        appointmentEntity.setService(serviceEntity);
        appointmentEntity.setMerchant(merchant);
        appointmentEntity.setCustomer(consumer);
        appointmentEntity.setStatus(AppointmentEntity.Status.NEW);
        appointmentEntity = appointmentRepository.save(appointmentEntity);

        return appointmentEntity.getId();
    }

    public AppointmentDTO getAppointmentById(Long appointmentId) {
        AppointmentEntity appointmentEntity = appointmentRepository.findById(appointmentId)
                .orElseThrow(RuntimeException::new);
        Long customerId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (!appointmentEntity.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException();
        }
        return AppointmentDTOMapper.map(appointmentEntity);
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

    public void confirmAppointment(Long appointmentId) {
        AppointmentEntity appointmentEntity = findMerchantsAppointment(appointmentId);
        if (appointmentEntity.getStatus() != AppointmentEntity.Status.NEW) {
            throw new RuntimeException();
        }
        appointmentEntity.setStatus(AppointmentEntity.Status.CONFIRMED);
        appointmentRepository.save(appointmentEntity);
    }

    public void declineAppointment(Long appointmentId) {
        AppointmentEntity appointmentEntity = findMerchantsAppointment(appointmentId);
        if (appointmentEntity.getStatus() != AppointmentEntity.Status.NEW) {
            throw new RuntimeException();
        }
        appointmentEntity.setStatus(AppointmentEntity.Status.DECLINED);
        appointmentRepository.save(appointmentEntity);
    }

    public void cancelAppointment(Long appointmentId) {
        AppointmentEntity appointmentEntity = findMerchantsAppointment(appointmentId);
        if (appointmentEntity.getStatus() != AppointmentEntity.Status.CONFIRMED) {
            throw new RuntimeException();
        }
        appointmentEntity.setStatus(AppointmentEntity.Status.CANCELLED);
        appointmentRepository.save(appointmentEntity);
    }

    private AppointmentEntity findMerchantsAppointment(Long appointmentId) {
        AppointmentEntity appointmentEntity = appointmentRepository.findById(appointmentId)
                .orElseThrow(RuntimeException::new);
        Long merchantId = ((AggregatorAppPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (!appointmentEntity.getMerchant().getId().equals(merchantId)) {
            throw new RuntimeException();
        }

        return appointmentEntity;
    }

}
