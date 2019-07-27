package io.webApp.springbootstarter.attachments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.webApp.springbootstarter.notes.Note;

/**
 * Interface class for Attachment table, inherits JpaRepository.
 * 
 * @author satishkumaranbalagan
 *
 */
public interface attachmentRepository extends JpaRepository<attachment, String> {

	@Query(value = "select a from attachment a where a.note = :note")
	public List<attachment> getAllAttachmentsForUser(Note note);

	List<attachment> findBynoteID(String noteID);

}
