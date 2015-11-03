# Example Infinitum Application #

This brief tutorial walks through the process of creating a basic notepad application using the Infinitum Framework. It covers some of the framework basics, such as saving and deleting domain objects, loading domain objects, injecting layouts and views, and binding event handlers.

1. Create a new Android Project called '`NotepadExample`', select the Build Target 'Android 2.3.3' (API level 10).

2. Download the latest distribution of Infinitum Framework from the [downloads page](http://code.google.com/p/infinitum-framework/downloads/list).

3. Add the Infinitum JAR and the Dexmaker dependency to the application's build path.

4. Create a new XML resource in `res/raw` called `infinitum.cfg.xml` with the following contents:

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE infinitum-configuration PUBLIC
"-//Infinitum/Infinitum Configuration DTD 1.0//EN"
"http://clarionmedia.com/infinitum/dtd/infinitum-configuration-1.0.dtd">

<infinitum-configuration>
    
    <application>
        <property name="debug">true</property>
    </application>
    
    <domain>
    </domain>
    
    <beans>
        <component-scan base-package="com.clarionmedia" />
    </beans>
    
    <sqlite>
        <property name="dbName">notepad</property> 
        <property name="dbVersion">1</property>
    </sqlite>

</infinitum-configuration>
```

5. In order to provide framework support, we will extend one of Infinitum's Activities. In this case, make `NotepadExampleActivity` extend `InfinitumListActivity`.

```
public class NotepadExampleActivity extends InfinitumListActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
```

6. Modify the layout `main.xml` to include a `ListView`.

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent">
     
    <ListView  
        android:id="@android:id/list"
        android:layout_width="fill_parent" 
        android:layout_height="wrap_content" />
     
    <TextView
        android:id="@android:id/empty"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="No Notes" />
     
</LinearLayout>
```

7. In order to set the `Activity` layout, we will use the InjectLayout annotation.

```
@InjectLayout(R.layout.main)
public class NotepadExampleActivity extends InfinitumListActivity {
```

8. The notepad application will have a single domain object, `Note`.

```
public class Note {
	
	private long mId;
	private String mName;
	private String mContents;
	
	public long getId() {
		return mId;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public String getContents() {
		return mContents;
	}
	
	public void setContents(String contents) {
		mContents = contents;
	}

}
```

9. The `Note` class needs to be added to the `domain` element in `infinitum.cfg.xml`.

```
<domain>
    <model resource="com.clarionmedia.notepadexample.domain.Note" />
</domain>
```

10. Create a new `Activity` that extends `InfinitumActivity` called `NoteTaker` (don't forget to add it to `AndroidManifest.xml`).

```
public class NoteTaker extends InfinitumActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}

```

11. Create a new layout for the `NoteTaker` `Activity` called `notetaker.xml`. The layout will include two `EditText`s for the note name and contents and two `Button`s for saving the note and discarding.

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="10dp">
    
    <EditText 
        android:id="@+id/noteName"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:hint="Name"
        android:singleLine="true"
        android:layout_marginBottom="10dp" />
    
    <EditText 
        android:id="@+id/noteContents"
        android:layout_width="fill_parent"
        android:layout_height="250dp"
        android:layout_marginBottom="10dp"
        android:gravity="top" />
    
    <LinearLayout 
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:weightSum="1">
        
        <Button 
            android:id="@+id/saveNote"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="0.5"
            android:text="Save Note" />
        
        <Button 
            android:id="@+id/discardNote"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="0.5"
            android:text="Discard Note" />
        
    </LinearLayout>

</LinearLayout>
```

12. Add the `EditText`s and `Button`s to the `NoteTaker` `Activity` and inject the respective `View`s and `Layout`.

```
@InjectLayout(R.layout.notetaker)
public class NoteTaker extends InfinitumActivity {
	
    @InjectView(R.id.noteName)
    private EditText mNoteName;
	
    @InjectView(R.id.noteContents)
    private EditText mNoteContents;
	
    @InjectView(R.id.saveNote)
    private Button mSaveNote;
	
    @InjectView(R.id.discardNote)
    private Button mDiscardNote;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
```

13. First, we will bind the discard event. The event callback needs to be implemented and the [Bind](Bind.md) annotation added to the appropriate `Button`.

```
private void discardNote(View view) {
    setResult(RESULT_CANCELED);
    finish();
}
```

```
@InjectView(R.id.discardNote)
@Bind(callback = "discardNote")
private Button mDiscardNote;
```

14. Next, we will take care of saving notes. It's recommended that SQLite operations occur asynchronously, so we will use Android's `AsyncTask` to persist notes. In `NoteTaker`, create an inner class, `SaveNoteAsyncTask`, which extends `AsyncTask`.

```
private class SaveNoteAsyncTask extends AsyncTask<Note, Void, Long> {

    @Override
    protected Long doInBackground(Note... note) {
        Session session = getInfinitumContext().getSession(DataSource.Sqlite);
        session.open();
	long id = session.save(note[0]);
	session.close();
	return id;
    }
		
    @Override
    protected void onPostExecute(Long id) {
        if (id > 0) {
            setResult(RESULT_OK);
	    finish();
	}
    }
		
}
```

15. Now create a callback for saving notes and bind it to the respective `Button`.

```
private void saveNote(View view) {
    String name = mNoteName.getText().toString().trim();
    String contents = mNoteContents.getText().toString();
    if (name.length() == 0)
        return;
    Note note = new Note();
    note.setName(name);
    note.setContents(contents);
    new SaveNoteAsyncTask().execute(note);
}
```

```
@InjectView(R.id.saveNote)
@Bind(callback = "saveNote")
private Button mSaveNote;
```

16. Let's wire up the `NoteTaker` `Activity` from `NotepadExampleActivity` by adding an options menu. First create `menu.xml` in `res/menu`.

```
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
   <item
      android:id="@+id/addNote"
      android:icon="@android:drawable/ic_menu_add"
      android:title="Add"/>
</menu>
```

17. Now add the menu to `NotepadExampleActivity`.

```
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
    return true;
}
    
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.addNote:
        Intent intent = new Intent(this, NoteTaker.class);
    	startActivityForResult(intent, 1);
        return true;
    default:
        return super.onOptionsItemSelected(item);
    }
}
```

18. We need to display the notes in the `ListView`, so let's create an `ArrayAdapter` for displaying them. Create a new class `NotesAdapter` that extends `ArrayAdapter`.

```
public class NotesAdapter extends ArrayAdapter<Note> {

    private List<Note> mNotes;
    private Context mContext;

    public NotesAdapter(Context context, int textViewResourceId, List<Note> notes) {
        super(context, textViewResourceId, notes);
	mNotes = notes;
	mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View rowView = inflater.inflate(R.layout.note, parent, false);
	TextView noteName = (TextView) rowView.findViewById(R.id.noteName);
	TextView noteContents = (TextView) rowView.findViewById(R.id.noteContents);
	Note note = mNotes.get(position);
	noteName.setText(note.getName());
	noteContents.setText(note.getContents());
	return rowView;
    }

}
```

19. Create the layout `note.xml` for displaying notes.

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="5dp">
    
    <TextView 
        android:id="@+id/noteName"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:textSize="18dp"
        android:layout_marginBottom="5dp" />
    
    <TextView 
        android:id="@+id/noteContents"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content" />

</LinearLayout>
```

20. Then create an `AsyncTask` for loading the notes and binding them to the `ListView` in `NotepadExampleActivity`.

```
private class LoadNotesAsyncTask extends AsyncTask<Void, Void, List<Note>> {

    @Override
    protected List<Note> doInBackground(Void... args) {
        Session session = getInfinitumContext().getSession(DataSource.Sqlite);
	session.open();
	List<Note> notes = session.createCriteria(Note.class).list();
	session.close();
	return notes;
    }
		
    @Override
    protected void onPostExecute(List<Note> notes) {
        NotesAdapter adapter = new NotesAdapter(NotepadExampleActivity.this, android.R.id.text1, notes);
	getListView().setAdapter(adapter);
    }
		
}
```

21. Update the `onCreate` method in `NotepadExampleActivity` to call the new `LoadNotesAsyncTask`.

```
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    new LoadNotesAsyncTask().execute();
}
```

22. Let's also add an `onActivityResult` method to handle newly added notes by refreshing the `ListView`.

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 1 && resultCode == RESULT_OK) {
        new LoadNotesAsyncTask().execute();
    }
}
```

23. We'll add the ability to delete notes by long-pressing on them and selecting delete from a context menu. First implement `onCreateContextMenu` in `NotepadExampleActivity`.

```
@Override
public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    if (v.getId() == android.R.id.list) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
        Note selected = (Note) getListView().getAdapter().getItem(info.position);
        menu.setHeaderTitle(selected.getName());
        menu.add(Menu.NONE, 0, 0, "Delete");
        menu.add(Menu.NONE, 1, 1, "Cancel");
    }
}
```

24. Implement an `AsyncTask` for deleting notes called `DeleteNoteAsyncTask`.

```
private class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Note> {

    @Override
    protected Note doInBackground(Note... note) {
        Session session = getInfinitumContext().getSession(DataSource.Sqlite);
	session.open();
	session.delete(note[0]);
	session.close();
	return note[0];
    }
		
    @Override
    protected void onPostExecute(Note note) {
        NotesAdapter adapter = (NotesAdapter) getListView().getAdapter();
	adapter.remove(note);
	adapter.notifyDataSetChanged();
    }
    	
}
```

25. Lastly, implement `onContextItemSelected`.

```
@Override
public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    int menuItemIndex = item.getItemId();
    Note note = (Note) getListView().getAdapter().getItem(info.position);
    if (menuItemIndex == 0) {
        new DeleteNoteAsyncTask().execute(note);
    }
    return true;
}
```

We can now create and delete notes, and we didn't write a single line of SQL! This application could easily be extended to support modifying notes by using `Session`'s `update` method.

The complete source code for this example application is available [here](https://code.google.com/p/infinitum-framework/source/browse/#svn%2Ftrunk%2FExamples%2FNotepadExample).