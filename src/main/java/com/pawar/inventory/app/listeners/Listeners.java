package com.pawar.inventory.app.listeners;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jboss.logging.Logger;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.app.events.PermissionDeleteEvent;
import com.pawar.inventory.app.events.RoleDeleteEvent;
import com.pawar.inventory.app.model.Permission;
import com.pawar.inventory.app.model.Role;
import com.pawar.inventory.app.repository.PermissionRepository;
import com.pawar.inventory.app.repository.RoleRepository;

@Service
public class Listeners {

	private static final Logger logger = Logger.getLogger(Listeners.class);

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	private static final String NEW_ROLE_TOPIC = "TO.DO.NEW.ROLE";
	private static final String NEW_PERMISSION_TOPIC = "TO.DO.NEW.PERMISSION";
	private static final String UPDATED_PERMISSION_TOPIC = "TO.DO.UPDATE.PERMISSION";
	private static final String UPDATED_ROLE_TOPIC = "TO.DO.UPDATE.ROLE";
	private static final String DELETE_PERMISSION_TOPIC = "TO.DO.DELETE.PERMISSION";
	private static final String DELETE_ROLE_TOPIC = "TO.DO.DELETE.ROLE";
	private static final String ASSIGN_ROLE_PERMISSION_TOPIC = "TO.DO.ASSIGN.ROLE.PERMISSION";
	private static final String UNASSIGN_ROLE_PERMISSION_TOPIC = "TO.DO.UNASSIGN.ROLE.PERMISSION";

	private final ObjectMapper mapper;

	public Listeners() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
	}

	@KafkaListener(topics = NEW_ROLE_TOPIC)
	public void roleListener(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack) {
		try {

			String key = consumerRecord.key();
			String value = consumerRecord.value();
			int partition = consumerRecord.partition();

			Role role = mapper.readValue(value, Role.class);
			logger.infof("Consumed message : " + value + " with key : " + key + " from partition : " + partition);
			logger.infof("role : {}", role);
			if (value != null) {
				roleRepository.save(role);
				logger.infof("Role saved to Menu database: {}", value);

				ack.acknowledge();
			} else {
				logger.warnf("Received null value from Kafka topic. {}", NEW_ROLE_TOPIC);
			}

		} catch (Exception e) {
			logger.errorf("errorf processing Kafka message: {}", e.getMessage());
		}

	}

	@KafkaListener(topics = UPDATED_ROLE_TOPIC)
	public void updateRoleListener(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack) {
		try {

			String key = consumerRecord.key();
			String value = consumerRecord.value();
			int partition = consumerRecord.partition();

			Role role = mapper.readValue(value, Role.class);
			logger.infof("Consumed message : " + value + " with key : " + key + " from partition : " + partition);
			logger.infof("Role : {}", role);
			if (value != null) {
				roleRepository.save(role);
				logger.infof("Role Updated to Menu database: {}", value);

				ack.acknowledge();
			} else {
				logger.warnf("Received null value from Kafka topic. {}", UPDATED_ROLE_TOPIC);
			}

		} catch (Exception e) {
			logger.errorf("errorf processing Kafka message: {}", e.getMessage());
		}

	}

	@KafkaListener(topics = ASSIGN_ROLE_PERMISSION_TOPIC)
	public void assignRolePermissionListener(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack) {
		try {

			String key = consumerRecord.key();
			String value = consumerRecord.value();
			int partition = consumerRecord.partition();

			Role role = mapper.readValue(value, Role.class);
			logger.infof("Consumed message : " + value + " with key : " + key + " from partition : " + partition);
			logger.infof("role : {}", role);
			if (value != null) {
				roleRepository.save(role);
				logger.infof("Role updated to Menu database: {}", value);

				ack.acknowledge();
			} else {
				logger.warnf("Received null value from Kafka topic. {}", ASSIGN_ROLE_PERMISSION_TOPIC);
			}

		} catch (Exception e) {
			logger.errorf("errorf processing Kafka message: {}", e.getMessage());
		}

	}

	@KafkaListener(topics = UNASSIGN_ROLE_PERMISSION_TOPIC)
	public void unassignRolePermissionListener(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack) {
		try {

			String key = consumerRecord.key();
			String value = consumerRecord.value();
			int partition = consumerRecord.partition();

			Role role = mapper.readValue(value, Role.class);
			logger.infof("Consumed message : " + value + " with key : " + key + " from partition : " + partition);
			logger.infof("role : {}", role);
			if (value != null) {
				roleRepository.save(role);
				logger.infof("Role updated to Menu database: {}", value);

				ack.acknowledge();
			} else {
				logger.warnf("Received null value from Kafka topic. {}", ASSIGN_ROLE_PERMISSION_TOPIC);
			}

		} catch (Exception e) {
			logger.errorf("errorf processing Kafka message: {}", e.getMessage());
		}

	}

	@KafkaListener(topics = DELETE_ROLE_TOPIC)
	public void deleteRoleListener(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack) {

		try {
			String key = consumerRecord.key();
			String value = consumerRecord.value();
			int partition = consumerRecord.partition();

			RoleDeleteEvent roleDeleteEvent = mapper.readValue(value, RoleDeleteEvent.class);
			Integer roleId = roleDeleteEvent.getRoleId();
			logger.infof("value : {}", value);
			logger.infof("Consumed message : " + roleId + " with key : " + key + " from partition : " + partition);
			if (value != null) {
				roleRepository.deleteById(roleId);
				logger.infof("Role deleted from Menu database : {}", roleId);

				ack.acknowledge();
			} else {
				logger.warnf("Received null value from Kafka topic. {}", DELETE_PERMISSION_TOPIC);
			}
		} catch (Exception e) {
			logger.errorf("errorf processing Kafka message: {}", e.getMessage());
			// Handle the exception (e.g., log, retry, or skip)
		}
	}

	@KafkaListener(topics = NEW_PERMISSION_TOPIC)
	public void permissionListener(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack) {

		try {
			String key = consumerRecord.key();
			String value = consumerRecord.value();
			int partition = consumerRecord.partition();

			Permission permission = mapper.readValue(value, Permission.class);

			logger.infof("value : {}", value);
			logger.infof("Consumed message : " + permission + " with key : " + key + " from partition : " + partition);
			if (value != null) {
				permissionRepository.save(permission);
				logger.infof("Permission saved to Menu database : {}", permission);

				ack.acknowledge();
			} else {
				logger.warnf("Received null value from Kafka topic. {}", NEW_PERMISSION_TOPIC);
			}
		} catch (Exception e) {
			logger.errorf("errorf processing Kafka message: {}", e.getMessage());
			// Handle the exception (e.g., log, retry, or skip)
		}
	}

	@KafkaListener(topics = UPDATED_PERMISSION_TOPIC)
	public void updatePermissionListener(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack) {

		try {
			String key = consumerRecord.key();
			String value = consumerRecord.value();
			int partition = consumerRecord.partition();

			Permission updatedPermission = mapper.readValue(value, Permission.class);

			logger.infof("value : {}", value);
			logger.infof("Consumed message : " + updatedPermission + " with key : " + key + " from partition : "
					+ partition);
			if (value != null) {
				permissionRepository.save(updatedPermission);
				logger.infof("Permission updated to Menu database : {}", updatedPermission);

				ack.acknowledge();
			} else {
				logger.warnf("Received null value from Kafka topic. {}", UPDATED_PERMISSION_TOPIC);
			}
		} catch (Exception e) {
			logger.errorf("errorf processing Kafka message: {}", e.getMessage());
			// Handle the exception (e.g., log, retry, or skip)
		}
	}

	@KafkaListener(topics = DELETE_PERMISSION_TOPIC)
	public void deletePermissionListener(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack) {

		try {
			String key = consumerRecord.key();
			String value = consumerRecord.value();
			int partition = consumerRecord.partition();

			PermissionDeleteEvent permissionDeleteEvent = mapper.readValue(value, PermissionDeleteEvent.class);
			Integer permissionId = permissionDeleteEvent.getPermissionId();
			logger.infof("value : {}", value);
			logger.infof("Consumed message : " + permissionId + " with key : " + key + " from partition : " + partition);
			if (value != null) {
				permissionRepository.deleteById(permissionId);
				logger.infof("Permission deleted from Menu database : {}", permissionId);

				ack.acknowledge();
			} else {
				logger.warnf("Received null value from Kafka topic. {}", DELETE_PERMISSION_TOPIC);
			}
		} catch (Exception e) {
			logger.errorf("errorf processing Kafka message: {}", e.getMessage());
			// Handle the exception (e.g., log, retry, or skip)
		}
	}

}
