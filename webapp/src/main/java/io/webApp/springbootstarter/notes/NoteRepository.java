package io.webApp.springbootstarter.notes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.webApp.springbootstarter.notes.Note;

/**
 * Interface class for Note table, inherits JpaRepository.
 * 
 * @author satishkumaranbalagan
 *
 */
public interface NoteRepository extends JpaRepository<Note, String> {

	List<Note> findByemailID(String emailID);

}
