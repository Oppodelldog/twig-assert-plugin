<?php

class TestTarget
{
    public $someField = '';

    public function getAnswer(): void
    {
    }

    public function getChild() : ChildClass
    {
        return new ChildClass();
    }
    
    public function setChild(ChildClass $child):void{
        
    }
}