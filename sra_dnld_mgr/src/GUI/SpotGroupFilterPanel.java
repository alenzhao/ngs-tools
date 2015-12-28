/* ===========================================================================
#
#                            PUBLIC DOMAIN NOTICE
#               National Center for Biotechnology Information
#
#  This software/database is a "United States Government Work" under the
#  terms of the United States Copyright Act.  It was written as part of
#  the author's official duties as a United States Government employee and
#  thus cannot be copyrighted.  This software/database is freely available
#  to the public for use. The National Library of Medicine and the U.S.
#  Government have not placed any restriction on its use or reproduction.
#
#  Although all reasonable efforts have been taken to ensure the accuracy
#  and reliability of the software and data, the NLM and the U.S.
#  Government do not and cannot warrant the performance or results that
#  may be obtained by using this software or data. The NLM and the U.S.
#  Government disclaim all warranties, express or implied, including
#  warranties of performance, merchantability or fitness for any particular
#  purpose.
#
#  Please cite the author in any work or product based on this material.
#
=========================================================================== */

package GUI;

import Bio.BioReadGroupEnumerator;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.SwingWorker.StateValue;

public class SpotGroupFilterPanel extends DlgPanel
    implements ActionListener, PropertyChangeListener
{
    static final long serialVersionUID = 1;
    
    private final JCheckBox checkbox;
    private final JComboBox<String> groups;
    private final JProgressBar progress;
    private BioReadGroupEnumerator enumerator;
            
    public String get_text()
    {
        if ( groups.getItemCount() > 0 )
        {
            return (String) groups.getSelectedItem();
        }
        else
            return "";
    }

    public void set_text( final String value, final String accession )
    {
        if ( accession != null )
        {
            boolean restart = true;
            if ( enumerator != null )
            {
                if ( !enumerator.isDone() ) enumerator.cancel( true );
                if ( enumerator.get_accession().equals( accession ) ) restart = false;
            }
            if ( restart )
            {
                groups.removeAllItems();
                enumerator = new BioReadGroupEnumerator( groups, accession, value );
                enumerator.addPropertyChangeListener( this );
                enumerator.execute();
            }
        }
    }

    public void set_editable( boolean value )
    {
        checkbox.setSelected( value );
        groups.setEnabled( value );
    }

    public boolean get_editable() { return checkbox.isSelected(); }
    
    @Override public void actionPerformed( ActionEvent e )
    {
        groups.setEnabled( checkbox.isSelected() );
    }

    @Override public void propertyChange( final PropertyChangeEvent event )
    {
        final String propname = event.getPropertyName();
        if ( propname.equals( "progress" ) )
        {
            progress.setValue( ( Integer )event.getNewValue() );
        }
        else if ( propname.equals( "state" ) )
        {
            switch ( (StateValue) event.getNewValue() )
            {
                case DONE    : progress.setVisible( false ); break;
                    
                case STARTED : 
                case PENDING : progress.setVisible( true ); break;
            }
        }
    }
    
    public SpotGroupFilterPanel( final String caption )
    {
        super( caption, DFLT_PANEL_WIDTH );
        
        JPanel p = new JPanel( new BorderLayout() );
        
        checkbox = make_checkbox( false );
        checkbox.addActionListener( this );
        p.add( checkbox, BorderLayout.LINE_START );
        
        groups = make_combo_box( false );
        p.add( groups, BorderLayout.CENTER );

        progress = new JProgressBar();
        progress.setVisible( false );
        progress.setIndeterminate( true );
        p.add( progress, BorderLayout.LINE_END );
                
        add( p, BorderLayout.CENTER );
        
        enumerator = null;
    }

}
