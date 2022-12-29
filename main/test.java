import java.util.*;
import java.io.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


abstract class User
{

    abstract void set_userName(String Username) throws IOException;
    abstract void set_userPassword(String Password) throws IOException;
    abstract String get_User_Name();
    abstract boolean checkValidLogin() throws IOException;

}

class AO extends User
{
    private String AO_name;
    private String AO_pass;
    private File AO_File;
    AO()
    {
        this.AO_name="";
        this.AO_pass="";
    }
    @Override
    void set_userName(String Username) throws IOException
    {
        this.AO_name=Username;
    }

    @Override
    void set_userPassword(String Password) throws IOException
    {
        this.AO_pass=Password;
    }

    @Override
    String get_User_Name()
    {
        return AO_name;
    }

    public String getAO_name()
    {
        return get_User_Name();
    }

    public String getAO_password()
    {
        return this.AO_pass;
    }

    @Override
    boolean checkValidLogin() throws IOException
    {
        boolean correct_login = false;
        try {
            BufferedReader login=new BufferedReader(new FileReader("AO_login.txt"));
            String username_, password_;
            username_ = login.readLine();
            password_ = login.readLine();

            while(username_ !=null && password_!=null)
            {
                if (Objects.equals(getAO_name(), username_) && Objects.equals(getAO_password(), password_))
                {
                    correct_login = true;
                    break;
                }
                username_ = login.readLine();
                password_=login.readLine();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing file");
            e.printStackTrace();
        }
        return correct_login;
    }
}
class Teacher extends User
{
    ArrayList<Evaluation> evaluations;
    private String Teacher_name;
    private String Teacher_pass;
    private File Teacher_evaluations;
    Teacher()
    {
        this.Teacher_name="";
        this.Teacher_pass="";
        evaluations=null;
        Teacher_evaluations=null;
    }

    Teacher(String name)
    {
        this.Teacher_name=name;
    }
    public void set_TeacherEvaluations()
    {
        Teacher_evaluations=new File(this.Teacher_name+"_Evaluations.txt");
    }
    public File get_TeacherEvaluations()
    {
        return Teacher_evaluations;
    }

    public void set_evalList(ArrayList<Evaluation>eval_list)
    {
        this.evaluations=new ArrayList<Evaluation>(eval_list);
    }

    @Override
    void set_userName(String Username) throws IOException
    {
        this.Teacher_name=Username;
    }

    public String getTeacher_name()
    {
        return this.Teacher_name;
    }

    @Override
    void set_userPassword(String Password) throws IOException
    {
        this.Teacher_pass=Password;
    }

    @Override
    String get_User_Name() {
        return getTeacher_name();
    }

    public String get_TeacherPassword()
    {
        return this.Teacher_pass;
    }
    @Override
    boolean checkValidLogin() throws IOException {
        boolean correct_login = false;
        try {
            BufferedReader login=new BufferedReader(new FileReader("teacher_login.txt"));
            String username_, password_;
            username_ = login.readLine();
            password_ = login.readLine();

            while(username_ !=null && password_!=null)
            {
                if (Objects.equals(getTeacher_name(), username_) && Objects.equals(get_TeacherPassword(), password_))
                {
                correct_login = true;
                break;
                }
                username_ = login.readLine();
                password_=login.readLine();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing file");
            e.printStackTrace();
        }
          return correct_login;
    }


    public void manage_Evaluations() throws IOException
    {
        try
        {
            FileWriter fw = new FileWriter(Teacher_evaluations, true);

            ListIterator<Evaluation> li =evaluations.listIterator();

            while(li.hasNext())
            {
                Evaluation e=(Evaluation) li.next();
                int no = e.get_Evaluation_no();
                String type = e.get_EvaluationType();

                fw.write(this.Teacher_name+"_evalno_"+no+"_"+type+".txt".toLowerCase());
                fw.write("\r\n");
            }
            fw.close();
        }
        catch (IOException e)
        {
            System.out.println("An error occurred while writing file");
            e.printStackTrace();
        }
    }

    public boolean remove_evaluation(Evaluation rem_obj) throws IOException
    {
        if(check_evaluation_exists(rem_obj))
        {
            File f=rem_obj.get_Eval_file();
            String remove_eval=f.getName();

            rem_obj.delete_Evaluation();

            File teacher_eval=this.get_TeacherEvaluations();

            if(teacher_eval.exists())
            {
                File temp_file = new File("temp.txt");
                BufferedReader my_reader = new BufferedReader(new FileReader(teacher_eval));
                BufferedWriter my_writer = new BufferedWriter(new FileWriter(temp_file));

                String current_line;
                boolean check = false;

                while ((current_line = my_reader.readLine()) != null)
                {
                    String trimmedLine = current_line.trim();
                    if (trimmedLine.equals(remove_eval))
                    {
                        check = true;
                    }
                    else
                    {
                        my_writer.write(current_line + System.getProperty("line.separator"));
                    }
                }

                my_writer.close();
                my_reader.close();
                teacher_eval.delete();                                        //delete original file
                boolean is_success = temp_file.renameTo(teacher_eval);        //rename the temporary file to original file

                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }




    public boolean check_evaluation_exists(Evaluation obj) throws IOException
    {
        boolean check=false;
        if(!obj.open_Evaluation(getTeacher_name())) {
            try {
                this.set_TeacherEvaluations();
                File eval_file=get_TeacherEvaluations();
                BufferedReader r=new BufferedReader(new FileReader(eval_file));

                File f=obj.get_Eval_file();

                String given_eval=f.getName();

                String t_eval="";
                String curr_line=r.readLine();
                while (curr_line!=null)
                {
                    t_eval = curr_line;
                    if (Objects.equals(t_eval,given_eval))
                    {
                        check=true;
                        break;
                    }
                    curr_line=r.readLine();
                }
                r.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            File temp_file=obj.get_Eval_file();
            temp_file.delete();
            check=false;
        }
        return check;
    }

    public boolean show_evaluation(Evaluation show_obj) throws IOException
    {
        if(check_evaluation_exists(show_obj))
        {
            File temp_file=show_obj.get_Eval_file();
            BufferedReader eval_reader = new BufferedReader(new FileReader(temp_file));

            String q_id="";
            String q_marks="";
            String q_des="";
            String q_clo="";
            String curr_line= eval_reader.readLine();
            int c=0;
            while(curr_line!=null)
            {
                System.out.print("---------------------Question No." + (c + 1) + "---------------------" + "\r\n");
                q_id=curr_line;
                System.out.println("Question id: "+q_id);
                curr_line=eval_reader.readLine();
                q_marks=curr_line;
                System.out.println("Question Marks: "+q_marks);
                curr_line=eval_reader.readLine();
                q_des=curr_line;
                System.out.println("Question Description: "+q_des);
                curr_line=eval_reader.readLine();
                q_clo=curr_line;
                System.out.println("CLO to Test: "+q_clo);
                curr_line=eval_reader.readLine();
                curr_line=eval_reader.readLine();
                c++;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean check_All_CLO_Has_Tested(Courses c) throws IOException
    {
        boolean tested = false;
        if(Teacher_evaluations.exists())
        {
            ArrayList<Integer> clo_list = c.getCourseCLOS();
            ListIterator<Integer> li = clo_list.listIterator();

            int testCount=0;
            while (li.hasNext())
            {
                Integer id = li.next();
                BufferedReader eval_reader = new BufferedReader(new FileReader(Teacher_evaluations));

                String curr_line1="";
                int count=0;
                curr_line1=eval_reader.readLine();
                while(curr_line1!=null)
                {
                    String open_eval=curr_line1;
                    File eval_f=new File(open_eval);
                    BufferedReader new_reader = new BufferedReader(new FileReader(eval_f));
                    String curr_line2="";
                    String skip_line="";
                    String clo_line="";
                    boolean check=false;
                    curr_line2= new_reader.readLine();
                    while(curr_line2!=null)
                    {
                        skip_line=new_reader.readLine();

                        skip_line=new_reader.readLine();

                        if(check)
                        {
                            skip_line=new_reader.readLine();
                            if(skip_line==null)
                            {
                                break;
                            }
                        }
                        check=true;
                        clo_line= new_reader.readLine();
                        if((Integer.parseInt(clo_line))==id)
                        {
                            count++;
                            if(count==2)
                            {
                                tested=true;
                                break;
                            }
                        }
                        curr_line2= new_reader.readLine();
                    }
                    new_reader.close();
                    if(tested)
                    {
                        testCount++;
                        System.out.println("CLO with id:"+id+" has been verified successfully");
                        tested=false;
                    }
                    curr_line1=eval_reader.readLine();
                }
                eval_reader.close();
            }
            if(testCount==clo_list.size())
            {
                tested=true;
            }
            else
            {
                tested=false;
            }
        }
        else
        {
            System.out.println("Evaluation do not exist");
        }
        return tested;
    }

}


class Evaluation
{
    ArrayList<Question> q_list;
    private int eval_no;
    private String e_type;
    private File eval_file;

    Evaluation(int no,String type)
    {
        this.eval_no = no;
        this.e_type = type;
    }

    public void set_questions(ArrayList<Question> eval_questions)
    {
        this.q_list=new ArrayList<Question>(eval_questions);
    }

    public boolean open_Evaluation(String teacher_name) throws IOException {
        this.eval_file = new File(teacher_name+"_evalno_"+get_Evaluation_no()+"_"+get_EvaluationType()+".txt".toLowerCase());
        if(this.eval_file.createNewFile())
        {
            return true;     //file created
        }
        else
        {
            return false;    //file already exists
        }
    }

    public void delete_Evaluation() throws IOException
    {
        this.eval_file.delete();
    }

    public int get_Evaluation_no()
    {
        return eval_no;
    }
    public File get_Eval_file()
    {
        return eval_file;
    }
    public String get_EvaluationType()
    {
        return e_type;
    }
    public void addQuestion() throws IOException
    {
        try {

            FileWriter fw = new FileWriter(eval_file, true);
            ListIterator<Question> li =q_list.listIterator();

            while(li.hasNext())
            {
                Question q=(Question) li.next();
                int clo=q.get_Question_clo();
                int id=q.get_Question_id();
                String des=q.get_Question_description();
                int m=q.get_Question_marks();
                fw.write(Integer.toString(id));
                fw.write("\r\n");
                fw.write(Integer.toString(m));
                fw.write("\r\n");
                fw.write(des);
                fw.write("\r\n");
                fw.write(Integer.toString(clo));
                    fw.write("\r\n");
                    fw.write("\r\n");

            }

            fw.close();
            System.out.println("Questions Added Successfully!");
        }
        catch (IOException e)
        {
            System.out.println("An error occurred while writing file");
            e.printStackTrace();
        }
    }
    public void removeQuestion() throws IOException
    {
        if(eval_file.exists())
        {
            File temp_file = new File("temp.txt");
            BufferedReader my_reader = new BufferedReader(new FileReader(eval_file));
            BufferedWriter my_writer = new BufferedWriter(new FileWriter(temp_file));

            String current_line;
            String skipped_line;
            boolean check = false;
            int no_of_lines_to_remove = 4;

            ListIterator<Question> li =q_list.listIterator();
            Question q = (Question) li.next();

            while ((current_line = my_reader.readLine()) != null)
            {
                String trimmedLine = current_line.trim();
                if (trimmedLine.equals(Integer.toString(q.get_Question_id())))
                {
                    check = true;
                    int i = 0;
                    while (i < no_of_lines_to_remove)
                    {
                        skipped_line = my_reader.readLine();
                        i++;
                    }
                }
                else
                {
                    my_writer.write(current_line + System.getProperty("line.separator"));
                }
            }
            if (check)
            {
                System.out.println("Question Removed Successfully !");
            }
            else
            {
                System.out.println("There is no such question exist in this Evaluation !");
            }

            my_writer.close();
            my_reader.close();
            eval_file.delete();                                        //delete original file
            boolean is_success = temp_file.renameTo(eval_file);        //rename the temporary file to original file
        }
        else
        {
            System.out.println("Such Evaluation Does Not Exists in which you try to remove question !");
        }
    }
    public void updateQuestion(String oldString, String newString) throws IOException
    {
        if(eval_file.exists())
        {

            File tempFile = new File("temp.txt");
            BufferedReader my_reader = new BufferedReader(new FileReader(eval_file));
            BufferedWriter my_writer = new BufferedWriter(new FileWriter(tempFile));

            String data;

            ListIterator<Question> li = q_list.listIterator();
            Question q = (Question) li.next();

            boolean check = false;
            boolean check2 = false;

            String oldContent = "";
            String line= my_reader.readLine();
            String existing_content="";
            while (line!= null)
            {
                data = line;
                oldContent = data;
                if (Objects.equals(data, Integer.toString(q.get_Question_id())))
                {
                    oldContent = data + System.lineSeparator();
                    check = true;
                    check2 = true;
                    int i = 0;
                    while (i < 3)
                    {
                        oldContent+= my_reader.readLine()+System.lineSeparator();
                        i++;
                    }
                    existing_content= oldContent;
                    oldContent =oldContent.replaceAll(oldString, newString);

                    if(existing_content.equals(oldContent))
                    {
                        check2=false;
                    }
                }
                if (check)
                {
                    my_writer.write(oldContent);
                    check = false;
                }
                else
                {
                    my_writer.write(oldContent + System.lineSeparator());
                }
                line=my_reader.readLine();
            }

            if (check2)
            {
                System.out.println("Question Updated Successfully !");
            }
            else if(!check2)
            {
                System.out.println("Existing Information Provided does not exists in this evaluation!");
            }
            else
            {
                System.out.println("There is no such question exist in this Evaluation !");
            }

            my_reader.close();
            my_writer.close();
            eval_file.delete();                                        //delete original file
            boolean isRenamed=tempFile.renameTo(eval_file);        //rename the temporary file to original file

        }
        else
        {
            System.out.println("Such Evaluation Does Not Exists in which you try to update question !");
        }
    }

    public boolean check_a_CLO_Has_Tested(int clo_id) throws IOException
    {
        boolean tested=false;
        if(eval_file.exists())
        {
            BufferedReader new_reader = new BufferedReader(new FileReader(eval_file));
            String curr_line="";
            String skip_line="";
            String clo_line="";
            String q_id="";
            int count=0;
            curr_line= new_reader.readLine();
            String tested_arr[];
            tested_arr= new String[2];

            while(curr_line!=null)
            {
                q_id=curr_line;
                skip_line=new_reader.readLine();
                skip_line=new_reader.readLine();
                clo_line= new_reader.readLine();
                if((Integer.parseInt(clo_line))==clo_id)
                {
                    count++;
                    if(count<=2)
                    {
                        tested_arr[count-1] = q_id;
                    }
                    if(count==2)
                    {
                        tested=true;
                        break;
                    }
                }

                skip_line=new_reader.readLine();
                curr_line=new_reader.readLine();
            }
            if(tested)
            {
                System.out.println("CLO with id "+clo_id+" has been tested successfully by Question no."+tested_arr[0]+" and Question no."+tested_arr[1]);
            }

            new_reader.close();
        }
        else
        {
            System.out.println("Evaluation do not exist");
        }
        return tested;
    }

}

class Question
{
    //attributes
    private int q_id;
    private String description;
    private int marks;
    private int clo_to_test;

    //methods
    Question(int id,String des,int marks,int clo)
    {
        this.q_id=id;
        this.description=des;
        this.marks=marks;
        this.clo_to_test=clo;
    }

    public int get_Question_id()
    {
        return q_id;
    }
    public String get_Question_description()
    {
        return description;
    }
    public int get_Question_marks()
    {
        return marks;
    }
    public int get_Question_clo()
    {
        return clo_to_test;
    }

}



class PLO{

    private int id;

    PLO(int id)
    {
        this.id=id;
    }



    void add_plo() throws IOException {

        Scanner s_str = new Scanner(System.in);
        String ploid=Integer.toString(this.id);
        File PLOList=new File("PLOList.txt");           //File for keeping record of all the PLO's
        BufferedReader PLOread=new BufferedReader(new FileReader(PLOList));


        String plocheck=" ";
        boolean checkDuplicatePLO=false;

        while (( plocheck= PLOread.readLine()) != null) {
            if(Objects.equals(plocheck,ploid))
            {
                checkDuplicatePLO=true;              //Check to avoid writing same PLO in the PLO file
            }

        }

        File plo_id=new File("plo_"+ploid+".txt".toLowerCase());        //File to keep record of all the courses for a certain PLO
        if(!plo_id.createNewFile())
            plo_id.createNewFile();

        if(!checkDuplicatePLO) {
            FileWriter fw = new FileWriter(PLOList, true);
            fw.write(ploid);
            fw.write("\r\n");
            fw.close();
        }
        else
        {
            System.out.println("PLO ID Already Exists...\n");
            return;
        }


    }


    List<String> getAllCourses() throws IOException {

        File PLOList = new File("PLO_"+this.id+".txt");
        if(PLOList.createNewFile())
        {
            System.out.println("PLO Does Not Exists");
            return null;
        }
        Scanner my_CLOListreader = new Scanner(PLOList);



        String s1 = "";
        String Course = "";
        List<String> AllPLOs = new ArrayList<>();
        while (my_CLOListreader.hasNextLine()) {
            Course = my_CLOListreader.nextLine();
            AllPLOs.add(Course);
        }
        System.out.println(AllPLOs);
        return AllPLOs;
    }
}
class Courses {
    private int id;
    private String name;
    private int credit_hours;
    private int PLO_id;
    private String type;

    private CLO clo;

    //multiple parameterized constructors for calling various functions of the course object
    Courses(int id, String name, int credit_hours, String type, int plo_id) throws FileNotFoundException, IOException {
        this.id = id;
        this.name = name;
        this.credit_hours = credit_hours;
        this.type = type;
        this.PLO_id=plo_id;

    }

    Courses(int id, String name, int credit_hours, String type) throws FileNotFoundException, IOException {
        this.id = id;
        this.name = name;
        this.credit_hours = credit_hours;
        this.type = type;


    }

    Courses(int id) throws FileNotFoundException, IOException {
        this.id = id;
    }

    Courses(int id,  CLO clo1) throws FileNotFoundException, IOException {
        this.id = id;
        this.clo = clo1;
    }


    //function for adding course
    void add_course() throws IOException {

        Scanner s_str = new Scanner(System.in);
        String ploid=Integer.toString(this.PLO_id);
        String courseid=Integer.toString(this.id);
        File PLOList=new File("PLOList.txt");           //File for keeping record of all the PLO's
        if(!PLOList.createNewFile())
            PLOList.createNewFile();

        BufferedReader PLOread=new BufferedReader(new FileReader(PLOList));

        String plocheck=" ";
        String CourseIDCheck="";
        boolean checkDuplicatePLO=false;
        boolean checkDuplicateCourse=false;

        while (( plocheck= PLOread.readLine()) != null) {
            if(Objects.equals(plocheck,ploid))
            {
                checkDuplicatePLO=true;              //Check to avoid writing same PLO in the PLO file
            }

        }

        File plo_id=new File("plo_"+ploid+".txt".toLowerCase());        //File to keep record of all the courses for a certain PLO
        if(!plo_id.createNewFile())
            plo_id.createNewFile();

        BufferedReader CourseIDRead=new BufferedReader(new FileReader(plo_id));
        while (( CourseIDCheck= CourseIDRead.readLine()) != null) {
            if(Objects.equals(CourseIDCheck,courseid))
            {
                checkDuplicateCourse=true;
            }

        }

        if(!checkDuplicatePLO) {
            FileWriter fw = new FileWriter(PLOList, true);
            fw.write(ploid);
            fw.write("\r\n");

            fw.close();
        }

        if(!checkDuplicateCourse){                          //Check to avoid writing duplicate course

            FileWriter fw_1=new FileWriter(plo_id,true);
            fw_1.write(courseid);
            fw_1.write("\r\n");
            fw_1.write(name);
            fw_1.write("\r\n");

            fw_1.close();

            File course_list=new File("CourseList.txt");
            FileWriter fw_2=new FileWriter(course_list,true);
            fw_2.write(Integer.toString(PLO_id));
            fw_2.write("\r\n");
            fw_2.write(Integer.toString(id));
            fw_2.write("\r\n");
            fw_2.write(name);
            fw_2.write("\r\n");
            fw_2.write(Integer.toString(credit_hours));
            fw_2.write("\r\n");
            fw_2.write(this.type);
            fw_2.write("\r\n");
            fw_2.close();
        }
        else
        {
            System.out.println("Course ID Already Exists...\n");
            return;
        }


    }
    //function for updating a course information
    void update_course() throws Exception{

        File CourseList= new File("CourseList.txt");
        String toUpdate=name+System.lineSeparator()+Integer.toString(credit_hours)+System.lineSeparator()+type+System.lineSeparator();
        File tempFile = new File("temp.txt");
        Scanner my_reader=new Scanner(CourseList);
        BufferedWriter my_writer = new BufferedWriter(new FileWriter(tempFile));
        String currentLine="";
        boolean found = false;

        while(my_reader.hasNextLine())
        {
            currentLine=my_reader.nextLine();
            my_writer.write(currentLine);
            my_writer.write("\r\n");
            if(Objects.equals(currentLine, Integer.toString(id))){

                my_writer.write(toUpdate);


                String s1=my_reader.nextLine();
                int l1=s1.length();
                String s2=my_reader.nextLine();
                int l2=s2.length();
                String s3=my_reader.nextLine();
                int l3=s3.length();
                found=true;


            }

        }

        my_writer.close();
        my_reader.close();


        CourseList.delete();
        tempFile.renameTo(CourseList);

        if(!found) {
            System.out.println("Course ID Not Found in Already Existing Courses ..\n");
        }



    }

    //function for removing a course
    void remove_course() throws IOException {
        File CourseList= new File("CourseList.txt");     //removing course information from courselist file
        File tempFile = new File("temp.txt");
        Scanner my_reader=new Scanner(CourseList);
        BufferedWriter my_writer = new BufferedWriter(new FileWriter(tempFile));
        String PLO="";
        String currentLine="";
        boolean found=false;
        while(my_reader.hasNextLine()) {
            PLO=my_reader.nextLine();
            currentLine=my_reader.nextLine();
            if (!Objects.equals(currentLine, Integer.toString(id))) {

                my_writer.write(PLO);
                my_writer.write("\r\n");
                my_writer.write(currentLine);
                my_writer.write("\r\n");
                String s1 = my_reader.nextLine();
                my_writer.write(s1);
                my_writer.write("\r\n");
                String s2 = my_reader.nextLine();
                my_writer.write(s2);
                my_writer.write("\r\n");
                String s3 = my_reader.nextLine();
                my_writer.write(s3);
                my_writer.write("\r\n");





            }
            else {

                File ploFile=new File("plo_"+PLO+".txt");        //removing course information from the File of course list for a certain PLO
                File tempPLO=new File("tempPLO.txt");
                Scanner my_reader_PLO=new Scanner(ploFile);
                BufferedWriter my_writer_PLO = new BufferedWriter(new FileWriter(tempPLO));
                String PLO_match="";
                found=true;
                while(my_reader_PLO.hasNextLine())
                {
                    PLO_match=my_reader_PLO.nextLine();
                    if(Objects.equals(PLO_match,currentLine)){
                        String s1=PLO_match;
                        String s2=my_reader_PLO.nextLine();

                    }
                    else {
                        my_writer_PLO.write(PLO_match);
                        my_writer_PLO.write("\r\n");
                    }
                }
                my_reader_PLO.close();
                my_writer_PLO.close();

                String s1 = my_reader.nextLine();

                String s2 = my_reader.nextLine();

                String s3 = my_reader.nextLine();

                boolean a= ploFile.delete();
                boolean b = tempPLO.renameTo(ploFile);
            }
        }
        if(!found)
        {
            System.out.println("Course ID Not Found in Already Existing Courses ..\n");
            return;
        }
        my_reader.close();
        my_writer.close();

        File CLO_Course=new File("CLO_Course_"+this.id+".txt");
        CLO_Course.delete();
        CourseList.delete();
        tempFile.renameTo(CourseList);

    }

    //function to add clo
    void add_CLO () throws IOException {

        Scanner s_str = new Scanner(System.in);
        String cloid=Integer.toString(clo.getId());
        String courseid=Integer.toString(this.id);
        File CLOList=new File("CLOList.txt");
        if(!CLOList.createNewFile())
            CLOList.createNewFile();
        BufferedReader CLOread=new BufferedReader(new FileReader(CLOList));
        String clocheck=" ";
        boolean checkDuplicate=false;
        boolean checkCourse=false;
        while (( clocheck= CLOread.readLine()) != null) {
            if(Objects.equals(clocheck,cloid))
            {
                checkDuplicate=true;
            }

        }

        if(!checkDuplicate) {
            FileWriter fw = new FileWriter(CLOList, true);
            fw.write(cloid);
            fw.write("\r\n");
            fw.close();
            File CourseList = new File("CourseList.txt");
            BufferedReader Courseread=new BufferedReader(new FileReader(CourseList));
            String course=Integer.toString(this.id);
            String coursecheck="";
            while((coursecheck=Courseread.readLine())!=null){
                if(coursecheck.equals(course))
                    checkCourse=true;

            }




            if(checkCourse){
                File clo_id = new File("CLO_Course_" + this.id + ".txt".toLowerCase());
                FileWriter fw_1 = new FileWriter(clo_id, true);
                String CLOID = String.valueOf(clo.getId());
                String CLODESC = clo.getDescription();
                fw_1.write(CLOID);
                fw_1.write("\r\n");
                fw_1.write(CLODESC);
                fw_1.write("\r\n");
                fw_1.close();
            }
            else {

                System.out.println("Course Does Not Exists...\n");
                return;


            }
        }
        else
            System.out.println("CLO ID Already Exists...\n");



    }
    //function to update CLO
    void update_CLO() throws IOException {

        File CourseCLOList= new File("CLO_Course_"+this.id+".txt".toLowerCase());
        String toUpdate=clo.getDescription()+System.lineSeparator();
        File tempFile = new File("tempCLO.txt");
        Scanner my_reader=new Scanner(CourseCLOList);
        BufferedWriter my_writer = new BufferedWriter(new FileWriter(tempFile));
        String currentLine="";
        boolean check=false;
        while(my_reader.hasNextLine())
        {
            currentLine=my_reader.nextLine();
            my_writer.write(currentLine);
            my_writer.write("\r\n");
            if(Objects.equals(currentLine, Integer.toString(clo.getId()))){
                check=true;
                my_writer.write(toUpdate);
                String s1=my_reader.nextLine();


            }

        }
        if(!check)
            System.out.println("CLO Does Not Exists...\n");
        my_writer.close();
        my_reader.close();

        CourseCLOList.delete();
        tempFile.renameTo(CourseCLOList);


    }
    //function to remove CLO
    void remove_CLO() throws IOException {

        File CourseCLOList= new File("CLO_Course_"+this.id+".txt".toLowerCase());
        String toUpdate=clo.getDescription()+System.lineSeparator();
        File tempFile = new File("tempCLO.txt");
        Scanner my_reader=new Scanner(CourseCLOList);
        BufferedWriter my_writer = new BufferedWriter(new FileWriter(tempFile));


        File CLOList= new File("CLOList.txt");
        File tempCLOFile = new File("tempCLOList.txt");
        Scanner my_CLOListreader=new Scanner(CLOList);
        BufferedWriter my_CLOListwriter = new BufferedWriter(new FileWriter(tempCLOFile));
        String CLOcheck="";
        String CLOID=Integer.toString(clo.getId());
        while((my_CLOListreader.hasNextLine()))
        {
            CLOcheck=my_CLOListreader.nextLine();
            if(!Objects.equals(CLOID,CLOcheck)){

                my_CLOListwriter.write(CLOcheck);
                my_CLOListwriter.write("\r\n");
            }


        }


        String currentLine="";
        boolean check=false;
        while(my_reader.hasNextLine())
        {
            currentLine=my_reader.nextLine();
            if(Objects.equals(currentLine, Integer.toString(clo.getId()))){
                check=true;
                String s1=my_reader.nextLine();


            }
            else{
                my_writer.write(currentLine);
                my_writer.write("\r\n");

            }

        }



        if(!check)
            System.out.println("CLO Does Not Exists...\n");

        my_writer.close();
        my_reader.close();

        my_CLOListwriter.close();
        my_CLOListreader.close();
        CLOList.delete();
        tempCLOFile.renameTo(CLOList);

        CourseCLOList.delete();
        tempFile.renameTo(CourseCLOList);

    }


    //Return List of All CLOs
    List<String> getAllCLOs() throws FileNotFoundException {
        File CLOList = new File("CLOList.txt");
        Scanner my_CLOListreader = new Scanner(CLOList);
        String s1 = "";
        String CLO = "";
        List<String> AllCLOs = new ArrayList<>();
        while (my_CLOListreader.hasNextLine()) {
            CLO = my_CLOListreader.nextLine();
            AllCLOs.add(CLO);

        }
        return AllCLOs;
    }

    //Return List of All CLOs of a Specific Course
    ArrayList<Integer> getCourseCLOS() throws IOException
    {
        File CourseList = new File("CourseList.txt");
        BufferedReader Courseread=new BufferedReader(new FileReader(CourseList));
        String course=Integer.toString(this.id);
        boolean checkCourse=false;
        String coursecheck="";
        while((coursecheck=Courseread.readLine())!=null)
        {
            if(coursecheck.equals(course))
                checkCourse=true;
        }

        if(checkCourse) {
            File CLOList = new File("CLO_Course_" + this.id + ".txt");
            Scanner my_CLOListreader = new Scanner(CLOList);
            String s1 = "";
            String CLO = "";
            String ignore = "";
            ArrayList<Integer> AllCLOs = new ArrayList<Integer>();
            while (my_CLOListreader.hasNextLine())
            {
                CLO = my_CLOListreader.nextLine();
                int clo_id=Integer.parseInt(CLO);
                ignore = my_CLOListreader.nextLine();
                AllCLOs.add(clo_id);
            }
            return AllCLOs;
        }
        else
        {
            System.out.println("Course ID Does Not Exists...\n");
        }
        return null;
    }
    //Printing List of all clos of a given course
    void PrintCourseCLO() throws IOException {
        File CourseList = new File("CourseList.txt");
        BufferedReader Courseread=new BufferedReader(new FileReader(CourseList));
        String course=Integer.toString(this.id);
        boolean checkCourse=false;
        String coursecheck="";
        while((coursecheck=Courseread.readLine())!=null){
            if(coursecheck.equals(course))
                checkCourse=true;

        }

        if(checkCourse) {
            File CLOList = new File("CLO_Course_" + this.id + ".txt");
            Scanner my_CLOListreader = new Scanner(CLOList);
            String s1 = "";
            String CLO = "";
            String ignore = "";
            List<String> AllCLOs = new ArrayList<>();
            while (my_CLOListreader.hasNextLine()) {
                CLO = my_CLOListreader.nextLine();
                ignore = my_CLOListreader.nextLine();
                AllCLOs.add(CLO);

            }

            System.out.println(AllCLOs);
        }
        else
            System.out.println("Course ID Does Not Exists...\n");

    }

}
class CLO {

    private int id;
    private String description;

    CLO(int ID, String Description){

        this.id=ID;
        this.description=Description;
    }
    int getId(){

        return this.id;
    }

    String getDescription(){
        return this.description;
    }
}






public class test
{
    public static void clear_screen()
    {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }



    public static void main(String[] args) throws Exception
    {
        clear_screen();
        System.out.println("-------------------------------Login--------------------------------");

        int l_choice=-1;
        Scanner s_int=new Scanner(System.in);
        Scanner s_str=new Scanner(System.in);

        do {
            System.out.println("1. Login as Admin Officer");
            System.out.println("2. Login as Teacher");
            System.out.println("0. Exit");

            System.out.print("Enter Your Choice : ");
            l_choice = s_int.nextInt();
            clear_screen();

            String username="";
            String password="";
            if (l_choice == 1)               //logged in as admin officer
            {
                User AO_user = new AO();
                boolean login_check;
                do {
                    System.out.print("Enter AO Name: ");
                    username = s_str.nextLine();
                    System.out.print("Enter Password: ");
                    password = s_str.nextLine();

                    AO_user.set_userName(username);
                    AO_user.set_userPassword(password);
                    login_check = AO_user.checkValidLogin();

                    if(login_check)
                    {
                        clear_screen();
                        System.out.print("***Login Successful***\n\n");
                    }
                    else
                    {
                        clear_screen();
                        System.out.print("***Invalid Username or Password***\n");
                    }

                }while(!login_check);

                int ao_choice=-1;
                do {
                    System.out.println("1. Add Course");
                    System.out.println("2. Update Course");
                    System.out.println("3. Remove Course");
                    System.out.println("4. Add CLO");
                    System.out.println("5. Update CLO");
                    System.out.println("6. Remove CLO");
                    System.out.println("7. Generate Relevant Courses for a PLO");
                    System.out.println("8. Generate List of All CLOs for a Course");
                    System.out.println("0. Go Back");
                    System.out.print("Enter Your Choice : ");
                    ao_choice = s_int.nextInt();
                    clear_screen();
                    if(ao_choice==1)
                    {
                        int temp_choice = -1;
                        do {
                            System.out.print("Enter PLO Id for the Course: ");
                            int PLO_id = s_int.nextInt();
                            System.out.print("Enter Course Id: ");
                            int course_id = s_int.nextInt();
                            System.out.print("Enter Course Name: ");
                            String course_name = s_str.nextLine();
                            System.out.print("Enter Credit Hours: ");
                            int course_ch = s_int.nextInt();
                            System.out.print("Enter Course Type(Core/Elective): ");
                            String course_type = s_str.nextLine();
                            Courses c1 = new Courses(course_id, course_name, course_ch, course_type, PLO_id);
                            c1.add_course();
                            System.out.println("Press 0 to Go Back");
                            int go_back = s_int.nextInt();
                            temp_choice = go_back;
                            if (temp_choice != 0)
                            {
                                System.out.println("Invalid Input. Please Enter Again.");
                            }
                        }while(temp_choice!=0);
                    }
                    else if(ao_choice==2)
                    {
                        int temp_choice = -1;
                        do {
                            System.out.print("Enter the ID of the Course You Want to Update: ");
                            int course_id = s_int.nextInt();
                            System.out.print("Enter the Updated Course Name: ");
                            String course_name = s_str.nextLine();
                            System.out.print("Enter the Updated Credit Hours: ");
                            int course_ch = s_int.nextInt();
                            System.out.print("Enter the Updated Course Type(Core/Elective): ");
                            String course_type = s_str.nextLine();
                            Courses c2 = new Courses(course_id, course_name, course_ch, course_type);
                            c2.update_course();
                            System.out.println("Press 0 to Go Back");
                            int go_back = s_int.nextInt();
                            temp_choice = go_back;
                            if (temp_choice != 0)
                            {
                                System.out.println("Invalid Input. Please Enter Again.");
                            }
                        }while(temp_choice!=0);
                    }
                    else if(ao_choice==3)
                    {
                        int temp_choice=-1;
                        do {
                            System.out.print("Enter the ID of the Course You Want to Delete: ");
                            int course_id = s_int.nextInt();
                            Courses c3 = new Courses(course_id);
                            c3.remove_course();
                            System.out.println("Press 0 to Go Back");
                            int go_back = s_int.nextInt();
                            temp_choice = go_back;
                            if (temp_choice != 0) {
                                System.out.println("Invalid Input. Please Enter Again.");
                            }
                        }while(temp_choice!=0);

                    }
                    else if(ao_choice==4)
                    {
                        int temp_choice=-1;
                        do {
                            System.out.print("Enter the ID of the Course You Want to Add CLO For: ");
                            int course_id = s_int.nextInt();
                            System.out.print("Enter the CLO ID: ");
                            int clo_id = s_int.nextInt();
                            System.out.print("Enter the CLO Description: ");
                            String clo_desc = s_str.nextLine();
                            CLO s = new CLO(clo_id, clo_desc);
                            Courses c1 = new Courses(course_id, s);
                            c1.add_CLO();
                            System.out.println("Press 0 to Go Back");
                            int go_back = s_int.nextInt();
                            temp_choice = go_back;
                            if (temp_choice != 0) {
                                System.out.println("Invalid Input. Please Enter Again.");
                            }
                        }while(temp_choice!=0);

                    }
                    else if(ao_choice==5)
                    {
                        int temp_choice=-1;
                        do {
                            System.out.print("Enter the ID of the Course You Want to Add CLO For: ");
                            int course_id = s_int.nextInt();
                            System.out.print("Enter the CLO ID: ");
                            int clo_id = s_int.nextInt();
                            System.out.print("Enter the CLO Description: ");
                            String clo_desc = s_str.nextLine();
                            CLO s = new CLO(clo_id, clo_desc);
                            Courses c1 = new Courses(course_id, s);
                            c1.update_CLO();
                            System.out.println("Press 0 to Go Back");
                            int go_back = s_int.nextInt();
                            temp_choice = go_back;
                            if (temp_choice != 0) {
                                System.out.println("Invalid Input. Please Enter Again.");
                            }
                        }while(temp_choice!=0);
                    }
                    else if(ao_choice==6)
                    {
                        int temp_choice=-1;
                        do {
                            System.out.print("Enter the ID of the Course You Want to Add CLO For: ");
                            int course_id = s_int.nextInt();
                            System.out.print("Enter the CLO ID: ");
                            int clo_id = s_int.nextInt();
                            CLO s = new CLO(clo_id, null);
                            Courses c1 = new Courses(course_id, s);
                            c1.remove_CLO();
                            System.out.println("Press 0 to Go Back");
                            int go_back = s_int.nextInt();
                            temp_choice = go_back;
                            if (temp_choice != 0) {
                                System.out.println("Invalid Input. Please Enter Again.");
                            }
                        }while(temp_choice!=0);

                    }
                    else if(ao_choice==6)
                    {
                        int temp_choice=-1;
                        do {
                            System.out.print("Enter the ID of the Course You Want to Add CLO For: ");
                            int course_id = s_int.nextInt();
                            System.out.print("Enter the CLO ID: ");
                            int clo_id = s_int.nextInt();
                            CLO s = new CLO(clo_id, null);
                            Courses c1 = new Courses(course_id, s);
                            c1.remove_CLO();
                            System.out.println("Press 0 to Go Back");
                            int go_back = s_int.nextInt();
                            temp_choice = go_back;
                            if (temp_choice != 0) {
                                System.out.println("Invalid Input. Please Enter Again.");
                            }
                        }while(temp_choice!=0);
                    }
                    else if(ao_choice==7)
                    {
                        int temp_choice=-1;
                        do {
                            System.out.print("Enter the PLO ID to Generate Courses List: ");
                            int plo_id = s_int.nextInt();
                            PLO c1 = new PLO(plo_id);
                            c1.getAllCourses();
                            System.out.println("Press 0 to Go Back");
                            int go_back = s_int.nextInt();
                            temp_choice = go_back;
                            if (temp_choice != 0) {
                                System.out.println("Invalid Input. Please Enter Again.");
                            }
                        }while(temp_choice!=0);
                    }
                    else if(ao_choice==8)
                    {
                        int temp_choice=-1;
                        do {
                            System.out.print("Enter the Course ID to Generate CLO List: ");
                            int clo_id = s_int.nextInt();
                            Courses c1 = new Courses(clo_id);
                            c1.PrintCourseCLO();
                            System.out.println("Press 0 to Go Back");
                            int go_back = s_int.nextInt();
                            temp_choice = go_back;
                            if (temp_choice != 0) {
                                System.out.println("Invalid Input. Please Enter Again.");
                            }
                        }while(temp_choice!=0);
                    }
                    else
                    {
                        if (ao_choice != 0)
                        {
                            System.out.println("Invalid Input. Please Enter Again.");
                        }
                    }
                    clear_screen();
                }while (ao_choice!=0);
            }
            else if(l_choice==2)            // logged in as teacher
            {
                User Teacher_user = new Teacher();
                boolean login_check;
                do {
                    System.out.print("Enter Teacher Name: ");
                    username = s_str.nextLine();
                    System.out.print("Enter Password: ");
                    password = s_str.nextLine();

                    Teacher_user.set_userName(username);
                    Teacher_user.set_userPassword(password);
                    login_check = Teacher_user.checkValidLogin();
                    if(login_check)
                    {
                        clear_screen();
                        System.out.print("***Login Successful***\n\n");
                    }
                    else
                    {
                        clear_screen();
                        System.out.print("***Invalid Username or Password***\n");
                    }

                }while(!login_check);
                username = Teacher_user.get_User_Name();
                    boolean temp=false;
                    int t_choice = -1;
                    do {
                        System.out.println("1. Manage Evaluation");
                        System.out.println("2. Show Evaluation");
                        System.out.println("3. Remove Evaluation");
                        System.out.println("4. Check All CLO are Tested");
                        System.out.println("0. Go Back");

                        System.out.print("Enter Your Choice : ");
                        t_choice = s_int.nextInt();
                        clear_screen();

                        Teacher t = null;

                        if (t_choice == 1) {

                            System.out.print("Enter Evaluation Id: ");
                            int eval_id = s_int.nextInt();
                            System.out.print("Enter Evaluation type: ");
                            String eval_t = s_str.nextLine();

                            clear_screen();

                            ArrayList<Evaluation> eval_list = new ArrayList<Evaluation>();
                            Evaluation eval = null;

                            int q_choice = -1;
                            do {
                                clear_screen();

                                System.out.println("1. Add Question");
                                System.out.println("2. Remove Question");
                                System.out.println("3. Update Question");
                                System.out.println("4. Check A CLO has Tested");
                                System.out.println("0. Go Back");

                                System.out.print("Enter Your Choice : ");
                                q_choice = s_int.nextInt();
                                temp=true;
                                clear_screen();

                                if (q_choice == 1) {
                                    int temp_choice=-1;
                                    do {
                                        System.out.print("Enter how many questions you want to add: ");
                                        int n = s_int.nextInt();

                                        ArrayList<Question> q_list = new ArrayList<Question>();
                                        ArrayList<Integer> q_id_list = new ArrayList<Integer>();
                                        boolean check = false;
                                        for (int i = 0; i < n; i++) {
                                            System.out.print("---------------------Question No." + (i + 1) + "---------------------" + "\r\n");

                                            System.out.print("Enter Question Id: ");
                                            int q_id = s_int.nextInt();
                                            if (check) {
                                                if (q_id_list.contains(q_id)) {
                                                    do {
                                                        System.out.println("The question with this id already exists! \r\n");
                                                        System.out.print("Please enter a different id: ");
                                                        q_id = s_int.nextInt();
                                                    } while (q_id_list.contains(q_id));

                                                }
                                                check = false;
                                            }

                                            if (!check) {
                                                System.out.print("Enter Question Marks: ");
                                                int q_marks = s_int.nextInt();

                                                System.out.print("Enter Description: ");
                                                String q_des = s_str.nextLine();

                                                System.out.print("Enter CLO to Test: ");
                                                int q_clo = s_int.nextInt();

                                                Question q = new Question(q_id, q_des, q_marks, q_clo);
                                                q_list.add(q);
                                                q_id_list.add(q_id);
                                            }
                                            check = true;
                                        }
                                        eval = new Evaluation(eval_id, eval_t);
                                        eval.set_questions(q_list);
                                        eval.open_Evaluation(username);
                                        eval.addQuestion();

                                        if (eval != null) {
                                            eval_list.add(eval);
                                            t = new Teacher(username);
                                            t.set_evalList(eval_list);
                                            t.set_TeacherEvaluations();
                                            t.manage_Evaluations();
                                        }
                                        System.out.println("Press 0 to Go Back");
                                        int go_back = s_int.nextInt();
                                        temp_choice = go_back;
                                        if (temp_choice != 0) {
                                            System.out.println("Invalid Input. Please Enter Again.");
                                        }
                                    }while(temp_choice!=0);
                                } else if (q_choice == 2) {
                                    int temp_choice=-1;
                                    do {
                                        System.out.print("Enter Question Id: ");
                                        int q_id = s_int.nextInt();

                                        ArrayList<Question> q_list = new ArrayList<Question>();

                                        Question q = new Question(q_id, "", 0, 0);
                                        q_list.add(q);
                                        eval = new Evaluation(eval_id, eval_t);
                                        eval.set_questions(q_list);
                                        eval.removeQuestion();
                                        System.out.println("Press 0 to Go Back");
                                        int go_back = s_int.nextInt();
                                        temp_choice = go_back;
                                        if (temp_choice != 0) {
                                            System.out.println("Invalid Input. Please Enter Again.");
                                        }
                                    }while(temp_choice!=0);
                                } else if (q_choice == 3) {
                                    int temp_choice=-1;
                                    do {
                                        int update_choice = -1;
                                        System.out.print("Enter Question Id to update: ");
                                        int q_id = s_int.nextInt();
                                        do {
                                            clear_screen();
                                            System.out.println("1. Update Question Description");
                                            System.out.println("2. Update Question Marks");
                                            System.out.println("3. Update CLO to Test");
                                            System.out.println("0. Go Back");

                                            System.out.print("Enter Your Choice : ");
                                            update_choice = s_int.nextInt();
                                            clear_screen();
                                            boolean check_update = false;
                                            String old = "";
                                            String updated = "";
                                            if (update_choice == 1) {
                                                int temp_choice1 = -1;
                                                do {
                                                    System.out.print("Enter Question Existing Description: ");
                                                    old = s_str.nextLine();
                                                    System.out.print("Enter Question Updated Description: ");
                                                    updated = s_str.nextLine();
                                                    check_update = true;
                                                    System.out.println("Press 0 to Go Back");
                                                    int go_back = s_int.nextInt();
                                                    temp_choice1 = go_back;
                                                    if (temp_choice1 != 0) {
                                                        System.out.println("Invalid Input. Please Enter Again.");
                                                    }
                                                }while(temp_choice1!=0);
                                            } else if (update_choice == 2) {
                                                int temp_choice1=-1;
                                                do {
                                                    System.out.print("Enter Question Existing Marks: ");
                                                    old = s_str.nextLine();
                                                    System.out.print("Enter Question Updated Marks: ");
                                                    updated = s_str.nextLine();
                                                    check_update = true;
                                                    System.out.println("Press 0 to Go Back");
                                                    int go_back = s_int.nextInt();
                                                    temp_choice1 = go_back;
                                                    if (temp_choice1 != 0) {
                                                        System.out.println("Invalid Input. Please Enter Again.");
                                                    }
                                                }while(temp_choice1!=0);
                                            } else if (update_choice == 3) {
                                                int temp_choice1=-1;
                                                do {
                                                    System.out.print("Enter Question Existing CLO: ");
                                                    old = s_str.nextLine();
                                                    System.out.print("Enter Question Updated CLO: ");
                                                    updated = s_str.nextLine();
                                                    check_update = true;
                                                    System.out.println("Press 0 to Go Back");
                                                    int go_back = s_int.nextInt();
                                                    temp_choice1 = go_back;
                                                    if (temp_choice1 != 0) {
                                                        System.out.println("Invalid Input. Please Enter Again.");
                                                    }
                                                }while(temp_choice1!=0);
                                            } else {
                                                if (update_choice != 0) {
                                                    System.out.println("Invalid Input. Please Enter Again.");
                                                }
                                            }
                                            if (check_update) {
                                                eval = new Evaluation(eval_id, eval_t);
                                                eval.open_Evaluation(username);
                                                eval.updateQuestion(old, updated);
                                            }
                                        } while (update_choice != 0);
                                        System.out.println("Press 0 to Go Back");
                                        int go_back = s_int.nextInt();
                                        temp_choice = go_back;
                                        if (temp_choice != 0) {
                                            System.out.println("Invalid Input. Please Enter Again.");
                                        }
                                    }while(temp_choice!=0);
                                } else if (q_choice == 4) {
                                    clear_screen();
                                    int test_choice = -1;
                                    do {
                                        eval = new Evaluation(eval_id, eval_t);
                                        boolean check = eval.open_Evaluation(username);
                                        if (check) {
                                            eval.delete_Evaluation();
                                            System.out.println("Such Evaluation does not exists in which you want to test a CLO!");
                                        } else {
                                            System.out.print("Enter CLO to test: ");
                                            int clo_id = s_int.nextInt();
                                            if (!eval.check_a_CLO_Has_Tested(clo_id)) {
                                                System.out.println("Clo with id no." + clo_id + " failed to test !");
                                            }
                                        }
                                        System.out.println("Press 0 to Go Back");
                                        int go_back = s_int.nextInt();
                                        test_choice = go_back;
                                        if (test_choice != 0) {
                                            System.out.println("Invalid Input. Please Enter Again.");
                                        }
                                    } while (test_choice != 0);
                                }
                                else
                                {
                                    if (q_choice != 0)
                                    {
                                        System.out.println("Invalid Input. Please Enter Again.");
                                    }
                                }

                            } while (q_choice != 0);
                        }
                        if (t_choice == 2) {
                            clear_screen();
                            System.out.print("Enter Evaluation Id: ");
                            int eval_id = s_int.nextInt();
                            System.out.print("Enter Evaluation type: ");
                            String eval_t = s_str.nextLine();
                            clear_screen();
                            t = new Teacher(username);
                            Evaluation show_eval = new Evaluation(eval_id, eval_t);
                            if (!t.show_evaluation(show_eval)) {
                                System.out.println("Such Evaluation does not exist!");
                            }
                            clear_screen();
                        }
                        if (t_choice == 3) {

                            int remove_choice = -1;

                            do {
                                clear_screen();
                                System.out.print("Enter Evaluation Id: ");
                                int eval_id = s_int.nextInt();
                                System.out.print("Enter Evaluation type: ");
                                String eval_t = s_str.nextLine();

                                t = new Teacher(username);

                                Evaluation rem_eval = new Evaluation(eval_id, eval_t);

                                if (t.remove_evaluation(rem_eval))
                                {
                                    System.out.println("Evaluation has been removed successfully!" + "\r\n");
                                }
                                else
                                {
                                    System.out.println("Such Evaluation does not exists which you want to remove!" + "\r\n");
                                }
                                System.out.println("Press 0 to Go Back");
                                int go_back = s_int.nextInt();
                                remove_choice = go_back;
                                if (remove_choice != 0) {
                                    System.out.println("Invalid Input. Please Enter Again.");
                                }
                            } while (remove_choice != 0);
                        }
                        if (t_choice == 4) {
                            clear_screen();

                            int test_choice = -1;
                            do {
                                t = new Teacher(username);
                                t.set_TeacherEvaluations();
                                System.out.print("Enter Course Id: ");
                                int c_id = s_int.nextInt();

                                Courses c = new Courses(c_id);
                                boolean check = t.check_All_CLO_Has_Tested(c);

                                if (check) {
                                    System.out.println("\r\n Therefore,All CLOS have have been verified successfully for this course!");
                                } else {
                                    System.out.println("But Not all the CLOS have verified for this course!");
                                }
                                System.out.println("Press 0 to Go Back");
                                int go_back = s_int.nextInt();
                                test_choice = go_back;
                                if (test_choice != 0) {
                                    System.out.println("Invalid Input. Please Enter Again.");
                                }
                            } while (test_choice != 0);
                        }
                        else {
                            if (t_choice != 0 && temp==false) {
                                System.out.println("Invalid Input. Please Enter Again.");
                            }
                        }
                    } while (t_choice != 0);
            }
            else
            {
                if(l_choice!=0)
                {
                    System.out.println("Invalid Input. Please Enter Again.");
                }
            }
        }while(l_choice!=0);
    }
}