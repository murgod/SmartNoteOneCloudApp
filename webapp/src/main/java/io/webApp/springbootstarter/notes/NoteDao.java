package io.webApp.springbootstarter.notes;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.webApp.springbootstarter.notes.Note;
import io.webApp.springbootstarter.notes.NoteRepository;

/**
 * Note table class with helper functions
 * 
 * @author satishkumaranbalagan
 *
 */
@Service
public class NoteDao {

	@Autowired
	NoteRepository noterepo;

	/**
	 * Save a Note
	 * 
	 * @param nt Note object
	 * @return the saved entity, should never be NULL
	 */
	public Note Save(Note nt) {
		return noterepo.save(nt);
	}

	/**
	 * get a all note
	 * 
	 * @return Notes in List
	 */
	public List<Note> findAll() {
		return noterepo.findAll();
	}

	/**
	 * get a note by ID
	 * 
	 * @param noteid in String
	 * @return Note object
	 */
	public Optional<Note> noteById(String noteid) {
		return noterepo.findById(noteid);
	}

	/**
	 * delete a note by Id
	 * 
	 * @param noteid in String
	 */
	public void deleteNote(String noteid) {
		noterepo.deleteById(noteid);
	}

	/**
	 * find list of notes under a email ID
	 * 
	 * @param emailID in String
	 * @return notes in List
	 */
	public List<Note> findByemailID(String emailID) {
		return noterepo.findByemailID(emailID);
	}

	/**
	 * find a note under a emailID based on note ID
	 * 
	 * @param NoteID  in String
	 * @param emailID in String
	 * @return Note object on success, else NULL
	 */
	public Note findNoteUnderEmailList(String NoteID, String emailID) {
		List<Note> NoteList = findByemailID(emailID);
		for (Note i : NoteList) {
			if (i.getId().equals(NoteID))
				return i;
		}
		return null;
	}

	/**
	 * delete a note under a emailID based on note ID
	 * 
	 * @param NoteID  in String
	 * @param emailID in String
	 * @return true on success, else false
	 */
	public boolean DeleteNoteUnderEmailList(String NoteID, String emailID) {
		List<Note> NoteList = findByemailID(emailID);
		for (Note i : NoteList) {
			if (i.getId().equals(NoteID)) {
				deleteNote(i.getId());
				return true;
			}
		}
		return false;
	}
}
